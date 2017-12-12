package com.syscxp.core.job;

import com.google.gson.*;
import com.syscxp.core.cloudbus.CloudBusEventListener;
import com.syscxp.core.cloudbus.EventSubscriberReceipt;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.GLock;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.thread.AsyncThread;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.message.GsonTransient;
import com.syscxp.header.message.Message;
import com.syscxp.utils.gson.GsonTypeCoder;
import com.syscxp.utils.gson.GsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.Component;
import com.syscxp.header.core.Completion;
import com.syscxp.header.core.NopeCompletion;
import com.syscxp.header.core.NopeReturnValueCompletion;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.managementnode.ManagementNodeChangeListener;
import com.syscxp.header.message.Event;
import com.syscxp.utils.Bucket;
import com.syscxp.utils.DebugUtils;
import com.syscxp.utils.JsonWrapper;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.serializable.SerializableHelper;

import javax.persistence.TypedQuery;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 */
public class JobQueueFacadeImpl2 implements JobQueueFacade, CloudBusEventListener, Component, ManagementNodeChangeListener {
    private static final CLogger logger = Utils.getLogger(JobQueueFacadeImpl2.class);
    private static final String LOCK_NAME = "JobQueueFacade.lock";
    private static final String ORPHAN_JOB_LOCK_NAME = "JobQueueFacade.orphanJobLock";
    private static final int LOCK_TIMEOUT = 60;

    private Map<Long, JobWrapper> jobWrappers = Collections.synchronizedMap(new HashMap<Long, JobWrapper>());

    private Map<Long, String> queueWrappers = Collections.synchronizedMap(new HashMap<Long, String>());

    private Future<Void> takeJobsCollector;

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ThreadFacade thdf;

    private volatile boolean stopped = false;
    private EventSubscriberReceipt unsubscriber;

    private class JobWire implements GsonTypeCoder<Job> {

        private final Gson gson = new GsonUtil().setCoder(Job.class, this).setExclusionStrategies(new ExclusionStrategy[]{
                new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        return fieldAttributes.getAnnotation(GsonTransient.class) != null;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                }
        }).create();

        @Override
        public Job deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jObj = jsonElement.getAsJsonObject();
            Map.Entry<String, JsonElement> entry = jObj.entrySet().iterator().next();
            String className = entry.getKey();
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(String.format("Unable to deserialize class[%s]", className), e);
            }
            return (Job) gson.fromJson(entry.getValue(), clazz);
        }

        @Override
        public JsonElement serialize(Job job, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jObj = new JsonObject();
            jObj.add(job.getClass().getName(), gson.toJsonTree(job));
            return jObj;
        }

        public String dumpMessage(Message msg) {
            return gson.toJson(msg, Message.class);
        }
    }

    private final JobWire wire = new JobWire();

    @Override
    public boolean handleEvent(Event e) {
        if (!(e instanceof JobEvent)) {
            return false;
        }

        JobEvent je = (JobEvent) e;
        JobWrapper jw = jobWrappers.get(je.getJobId());
        if (jw == null) {
            return false;
        }

        if (je.isSuccess()) {
            Object ret = je.getReturnValue() != null ? je.getReturnValue().get() : null;
            jw.success(ret);
        } else {
            jw.fail(je.getErrorCode());
        }

        return false;
    }

    @Override
    public boolean start() {
        unsubscriber = bus.subscribeEvent(this, new JobEvent());
        stopped = false;
        startTakeJobs();
        return true;
    }

    @Override
    public boolean stop() {
        stopped = true;
        if (unsubscriber != null) {
            unsubscriber.unsubscribeAll();
        }
        takeJobsCollector.cancel(true);
        return true;
    }

    private void startTakeJobs() {
        logger.debug("start take jobs");
        takeJobsCollector = thdf.submitPeriodicTask(new PeriodicTask() {

            @Override
            public void run() {
                if (jobWrappers.isEmpty()){
                    takeOverJobs();
                    takeOverLeftNodeJobs(null);
                }
            }

            @Override
            public TimeUnit getTimeUnit() {
                return TimeUnit.SECONDS;
            }

            @Override
            public long getInterval() {
                return 60 * 30; // 30 minute
            }

            @Override
            public String getName() {
                return "StartTakeJobsThread";
            }

        }, 60);
    }

    private void restartQueue(JobQueueVO qvo, String mgmtId) {
        SimpleQuery<JobQueueEntryVO> q = dbf.createQuery(JobQueueEntryVO.class);
//        q.select(JobQueueEntryVO_.id, JobQueueEntryVO_.name);
//        q.add(JobQueueEntryVO_.jobQueueId, SimpleQuery.Op.EQ, qvo.getId());
//        q.add(JobQueueEntryVO_.issuerManagementNodeId, SimpleQuery.Op.NULL);
//        List<Tuple> ts = q.listTuple();
//        for (Tuple t : ts) {
//            logger.debug(String.format("[Job Removed]: job[id:%s, name:%s] because its issuer management node[id:%s] became available", t.get(0), t.get(1), mgmtId));
//            dbf.removeByPrimaryKey((Long) t.get(0), JobQueueEntryVO.class);
//        }

        q = dbf.createQuery(JobQueueEntryVO.class);
        q.add(JobQueueEntryVO_.state, SimpleQuery.Op.IN, JobState.Pending, JobState.Processing, JobState.Error);
        q.add(JobQueueEntryVO_.jobQueueId, SimpleQuery.Op.EQ, qvo.getId());
        q.orderBy(JobQueueEntryVO_.id, SimpleQuery.Od.ASC);
        long count = q.count();
        if (count == 0) {
            logger.debug(String.format("[JobQueue Removed]: id:%s, no Pending or Processing job remaining in this queue, remove it", qvo.getId()));
            return;
        }

        List<JobQueueEntryVO> es = q.list();
        for (JobQueueEntryVO e : es) {
            if (e.getState() == JobState.Processing && !e.isRestartable()) {
                dbf.remove(e);
                JobEvent evt = new JobEvent();
                evt.setErrorCode(errf.instantiateErrorCode(SysErrors.MANAGEMENT_NODE_UNAVAILABLE_ERROR,
                        String.format("management node[id:%s] becomes unavailable, job[name:%s, id:%s] is not restartable", mgmtId, e.getName(), e.getId())));
                bus.publish(evt);
                logger.debug(String.format("[Job Removed]: job[id:%s, name:%s] because it's not restartable",
                        e.getId(), e.getName()));
                continue;
            }

            logger.debug(String.format("[Job Restart]: job[id:%s, name:%s] in queue[id:%s] is restarting as its previous worker node[id:%s] became unavailable",
                    e.getId(), e.getName(), qvo.getId(), mgmtId));
            execute(qvo.getName(), qvo.getOwner(), e, new NopeReturnValueCompletion(), null);
            return;
        }
    }

    private void takeOverJobs() {
        GLock lock = new GLock(ORPHAN_JOB_LOCK_NAME, LOCK_TIMEOUT);
        lock.lock();
        try {
            logger.debug(String.format("management node[id:%s] starts taking over jobs of left management node[%s]",
                    Platform.getManagementServerId(), Platform.getManagementServerId()));
            SimpleQuery<JobQueueVO> qq = dbf.createQuery(JobQueueVO.class);
            qq.add(JobQueueVO_.workerManagementNodeId, SimpleQuery.Op.EQ, Platform.getManagementServerId());
            List<JobQueueVO> queues = qq.list();

            logger.debug(String.format("[Orphan Queue found]: management node is going to take over %s orphan queues", queues.size()));
            for (JobQueueVO queue : queues) {
                restartQueue(queue, Platform.getManagementServerId());
            }
        } finally {
            lock.unlock();
        }
    }

    private void takeOverLeftNodeJobs(String mgmtId) {
        GLock lock = new GLock(ORPHAN_JOB_LOCK_NAME, LOCK_TIMEOUT);
        lock.lock();
        try {
            logger.debug(String.format("management node[id:%s] starts taking over jobs of left management node[%s]",
                    Platform.getManagementServerId(), mgmtId));
            SimpleQuery<JobQueueVO> qq = dbf.createQuery(JobQueueVO.class);
            qq.add(JobQueueVO_.workerManagementNodeId, SimpleQuery.Op.NULL);
            List<JobQueueVO> queues = qq.list();

            logger.debug(String.format("[Orphan Queue found]: management node is going to take over %s orphan queues", queues.size()));
            for (JobQueueVO queue : queues) {
                restartQueue(queue, mgmtId);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void nodeJoin(String nodeId) {
    }

    @Override
    public void nodeLeft(String nodeId) {
        takeOverLeftNodeJobs(nodeId);
    }

    @Override
    public void iAmDead(String nodeId) {
    }

    @Override
    public void iJoin(String nodeId) {
    }

    private interface JobWrapper {
        void run();

        void success(Object ret);

        void fail(ErrorCode err);
    }


    private <T> void execute(final String queueName, final String owner, final JobQueueEntryVO entry, final ReturnValueCompletion<T> completion, final Class<? extends T> returnType) {
        new JobWrapper() {
            private Long myJobId;

            @Transactional
            private JobQueueVO saveJob() throws IOException {
                JobQueueVO ret = null;
                String sql = "select queue from JobQueueVO queue where queue.name = :queueName";
                TypedQuery<JobQueueVO> q = dbf.getEntityManager().createQuery(sql, JobQueueVO.class);
                q.setParameter("queueName", queueName);
                JobQueueVO qvo = null;
                try {
                    qvo = q.getSingleResult();
                } catch (EmptyResultDataAccessException ne) {
                    // no queue yet
                }

                if (qvo == null) {
                    qvo = new JobQueueVO();
                    qvo.setName(queueName);
                    qvo.setOwner(owner);
                    qvo.setWorkerManagementNodeId(Platform.getManagementServerId());
                    dbf.getEntityManager().persist(qvo);
                    dbf.getEntityManager().flush();
                    dbf.getEntityManager().refresh(qvo);
                    logger.debug(String.format("[JobQueue created] id: %s, owner: %s, queue name: %s", qvo.getId(), owner, queueName));
                    ret = qvo;
                } else if (qvo.getWorkerManagementNodeId() == null) {
                    qvo.setWorkerManagementNodeId(Platform.getManagementServerId());
                    dbf.getEntityManager().merge(qvo);
                    ret = qvo;
                }

                entry.setJobQueueId(qvo.getId());
                entry.setIssuerManagementNodeId(Platform.getManagementServerId());
                entry.setState(JobState.Pending);

                JobQueueEntryVO ne = dbf.getEntityManager().merge(entry);
                dbf.getEntityManager().flush();
                dbf.getEntityManager().refresh(ne);
                logger.debug(String.format("[Job added] job queue name: %s, job class name: %s, job id: %s", qvo.getName(), ne.getName(),
                        ne.getId()));

                myJobId = ne.getId();
                jobWrappers.put(myJobId, this);
                return ret;
            }

            private void jobFail(JobQueueEntryVO jvo, ErrorCode err) {
                jvo.setDoneDate(new Timestamp(new Date().getTime()));
                jvo.setState(JobState.Error);
                jvo.setErrText(err.getDetails());
                dbf.update(jvo);

                JobEvent evt = new JobEvent();
                evt.setJobId(jvo.getId());
                evt.setErrorCode(err);
                bus.publish(evt);
            }

            private void jobDone(JobQueueEntryVO jvo, Object ret) {
                jvo.setDoneDate(new Timestamp(new Date().getTime()));
                jvo.setState(JobState.Completed);
                dbf.update(jvo);

                JobEvent evt = new JobEvent();
                evt.setJobId(jvo.getId());
                if (ret != null) {
                    evt.setReturnValue(JsonWrapper.wrap(ret));
                }
                bus.publish(evt);
            }

            private JobQueueEntryVO findJob(JobQueueVO qvo) {
                SimpleQuery<JobQueueEntryVO> q = dbf.createQuery(JobQueueEntryVO.class);
                q.add(JobQueueEntryVO_.state, SimpleQuery.Op.EQ, JobState.Pending);
                q.add(JobQueueEntryVO_.jobQueueId, SimpleQuery.Op.EQ, qvo.getId());
                q.setLimit(1);
                q.orderBy(JobQueueEntryVO_.id, SimpleQuery.Od.ASC);

                JobQueueEntryVO ev = q.find();

                if (ev == null){
                    q.add(JobQueueEntryVO_.state, SimpleQuery.Op.EQ, JobState.Error);
                    q.add(JobQueueEntryVO_.jobQueueId, SimpleQuery.Op.EQ, qvo.getId());
                    q.add(JobQueueEntryVO_.restartable, SimpleQuery.Op.EQ, true);
                    q.add(JobQueueEntryVO_.doneDate, SimpleQuery.Op.LT, Timestamp.valueOf(LocalDateTime.now().minusHours(1)));
                    q.setLimit(1);
                    q.orderBy(JobQueueEntryVO_.takenDate, SimpleQuery.Od.ASC);

                    ev = q.find();
                }

                return ev;
            }

            private boolean existHistoryErrorJobQueueEntry(JobQueueVO qvo) {
                SimpleQuery<JobQueueEntryVO> q = dbf.createQuery(JobQueueEntryVO.class);
                q.add(JobQueueEntryVO_.state, SimpleQuery.Op.EQ, JobState.Error);
                q.add(JobQueueEntryVO_.jobQueueId, SimpleQuery.Op.EQ, qvo.getId());
                q.add(JobQueueEntryVO_.restartable, SimpleQuery.Op.EQ, true);
                q.setLimit(1);
                return q.find() != null;
            }

            private Bucket takeJob(final JobQueueVO qvo) {
                GLock lock = new GLock(LOCK_NAME, LOCK_TIMEOUT);
                lock.lock();
                try {
                    JobQueueEntryVO jobe = findJob(qvo);
                    if (jobe == null) {
                        // nothing to do, release queue
                        if (! existHistoryErrorJobQueueEntry(qvo)) {    // 也没有error的需要再次执行的job
                            dbf.remove(qvo);
                        }
                        logger.debug(String.format("[JobQueue released, no pending or 1 hour ago error task, delete the queue] last owner: %s, queue name: %s, queue id: %s",
                                qvo.getOwner(), qvo.getName(), qvo.getId()));
                        return null;
                    }

                    while (true) {
                        try {
                            JobContextObject ctx = SerializableHelper.readObject(jobe.getContext());
                            Job theJob = ctx.load();
                            jobe.setState(JobState.Processing);
                            jobe.setTakenDate(new Timestamp(new Date().getTime()));
                            jobe.setTakenTimes(jobe.getTakenTimes() + 1);
                            jobe = dbf.updateAndRefresh(jobe);
                            return Bucket.newBucket(jobe, theJob);
                        } catch (Exception e1) {
                            String err = String.format("[Job de-serialize failed, the job will be marked as Error] queue name: %s, job id: %s, %s", qvo.getName(),
                                    jobe.getId(), e1.getMessage());
                            logger.warn(err, e1);
                            jobFail(jobe, errf.stringToInternalError(err));
                            jobe = findJob(qvo);
                            if (jobe == null){
                                return null;
                            }
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }

            @AsyncThread
            private void process(final JobQueueVO qvo) {

                if (stopped) {
                    logger.warn(String.format("[Job Facade Stopped]: stop processing job"));
                    return;
                }

                Bucket ret = takeJob(qvo);
                if (ret == null) {
                    return;
                }

                final JobQueueEntryVO e = ret.get(0);
                final Job job = ret.get(1);

                logger.debug(String.format("[Job Start] start executing job[id:%s, name:%s]", e.getId(), e.getName()));
                job.run(new ReturnValueCompletion<Object>(null) {
                    @Override
                    public void success(Object returnValue) {
                        try {
                            jobDone(e, returnValue);
                            logger.debug(String.format("[Job Success] job[id:%s, name:%s] succeed", e.getId(), e.getName()));
                        } catch (Throwable t){
                            logger.warn(String.format("unhandled exception happened when calling %s", job.getClass().getName()), t);
                            jobFail(e, errf.stringToInternalError(t.getMessage()));
                        } finally {
                            process(qvo);
                        }
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        try {
                            jobFail(e, errorCode);
                            logger.debug(String.format("[Job Failure] job[id:%s, name:%s] failed", e.getId(), e.getName()));
                        } catch (Throwable t){
                            logger.warn(String.format("unhandled exception happened when calling %s", job.getClass().getName()), t);
                            jobFail(e, errf.stringToInternalError(t.getMessage()));
                        } finally {
                            process(qvo);
                        }
                    }
                });
            }

            @Override
            public void run() {
                if (stopped) {
                    logger.warn(String.format("[Job Facade Stopped]: skip to run job[queueName:%s, owner:%s, name:%s]",
                            queueName, owner, entry.getName()));
                    return;
                }

                try {
                    GLock lock = new GLock(LOCK_NAME, LOCK_TIMEOUT);
                    JobQueueVO qvo = null;
                    lock.lock();
                    try {
                        qvo = saveJob();
                    } finally {
                        lock.unlock();
                    }

                    if (qvo != null) {
                        process(qvo);
                    }
                } catch (IOException e1) {
                    throw new CloudRuntimeException(String.format("unable to serialize job: %s", entry.getName()), e1);
                }
            }

            @Override
            public void success(Object ret) {
                DebugUtils.Assert(myJobId!=null, "how can myJobId be null???");
                jobWrappers.remove(myJobId);
                completion.success((T)ret);
            }

            @Override
            public void fail(ErrorCode err) {
                DebugUtils.Assert(myJobId!=null, "how can myJobId be null???");
                jobWrappers.remove(myJobId);
                completion.fail(err);
            }
        }.run();
    }

    @Override
    public <T> void execute(final String queueName, final String owner, final Job job, final ReturnValueCompletion<T> completion, final Class<? extends T> returnType) {
        try {
            JobQueueEntryVO e = new JobQueueEntryVO();
            JobContextObject ctx = new JobContextObject(job);
            byte[] bits = SerializableHelper.writeObject(ctx);
            e.setJobData(RESTApiDecoder.dump(job));
            e.setContext(bits);
            e.setRestartable(job.getClass().isAnnotationPresent(RestartableJob.class));
            e.setName(job.getClass().getName());
            execute(queueName, owner, e, completion, returnType);
        } catch (IOException e1) {
            throw new CloudRuntimeException(e1);
        }
    }

    @Override
    public void execute(String queueName, String owner, Job job, final Completion completion) {
        execute(queueName, owner, job, new ReturnValueCompletion<Object>(completion) {
            @Override
            public void success(Object returnValue) {
                completion.success();
            }

            @Override
            public void fail(ErrorCode errorCode) {
                completion.fail(errorCode);
            }
        }, null);
    }

    public void execute(String queueName, String owner, Job job) {
        execute(queueName, owner, job, new NopeCompletion());
    }

    @Override
    public void deleteJobQueue(String queueName) {

    }

    @Override
    public void evictOwner(String owner) {

    }

    @Override
    public List<String> listAllQueue() {
        return null;
    }

    @Override
    public List<String> listQueue(String namePattern) {
        return null;
    }

    @Override
    public long getPendingJobNumber(String queueName) {
        return 0;
    }

    @Override
    public List<String> listQueueHasPendingJob() {
        return null;
    }

    @Override
    public boolean startQueueIfPendingJob(String queueName, String owner) {
        return false;
    }

    @Override
    public boolean startQueueIfPendingJob(String queueName, String owner, boolean newThread) {
        return false;
    }
}
