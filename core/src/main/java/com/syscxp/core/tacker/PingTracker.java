package com.syscxp.core.tacker;

import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusSteppingCallback;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.Component;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.message.NeedReplyMessage;
import com.syscxp.utils.DebugUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 */
public abstract class PingTracker implements Component {
    public abstract String getResourceName();
    public abstract NeedReplyMessage getPingMessage(String resUuid);
    public abstract int getPingInterval();
    public abstract int getParallelismDegree();
    public abstract void handleReply(String resourceUuid, MessageReply reply);

    private final static CLogger logger = Utils.getLogger(PingTracker.class);

    private final List<String> resourceUuids = Collections.synchronizedList(new ArrayList<String>());
    private Set<String> resourceInTracking = Collections.synchronizedSet(new HashSet<String>());
    private Future<Void> trackerThread = null;

    @Autowired
    protected CloudBus bus;
    @Autowired
    protected ThreadFacade thdf;

    private class Tracker implements PeriodicTask {
        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getInterval() {
            return getPingInterval();
        }

        @Override
        public String getName() {
            return String.format("pingTracker-for-%s-managementNode-%s", getResourceName(), Platform.getManagementServerId());
        }

        @Override
        public void run() {
            try {
                List<NeedReplyMessage> msgs = null;
                synchronized (resourceUuids) {
                    final Map<NeedReplyMessage, String> tmp = new HashMap<NeedReplyMessage, String>();

                    msgs = new ArrayList<NeedReplyMessage>();
                    for (String resUuid : resourceUuids) {
                        if (resourceInTracking.contains(resUuid)) {
                            continue;
                        }

                        NeedReplyMessage msg = getPingMessage(resUuid);
                        msgs.add(msg);
                        resourceInTracking.add(resUuid);
                        tmp.put(msg, resUuid);
                    }

                    if (msgs.isEmpty()) {
                        return;
                    }

                    bus.send(msgs, getParallelismDegree(), new CloudBusSteppingCallback(null) {
                        @Override
                        public void run(NeedReplyMessage msg, MessageReply reply) {
                            String resUuid = tmp.get(msg);
                            DebugUtils.Assert(resUuid!=null, "where is my resource uuid???");
                            try {
                                handleReply(resUuid, reply);
                            } finally {
                                resourceInTracking.remove(resUuid);
                            }
                        }
                    });
                }
            } catch (Throwable t) {
                logger.warn("unhandled throwable", t);
            }
        }
    }

    protected void trackHook(String resourceUuid) {
    }
    
    protected void untrackHook(String resourceUuid) {
    }

    protected void startHook() {
    }

    protected void pingIntervalChanged() {
        startTracker();
    }
    
    public void track(String resUuid) {
        synchronized (resourceUuids) {
            if (!resourceUuids.contains(resUuid)) {
                resourceUuids.add(resUuid);
                trackHook(resUuid);
                logger.debug(String.format("start tracking %s[uuid:%s]", getResourceName(), resUuid));
            }
        }
    }

    public void untrackAll() {
        synchronized (resourceUuids) {
            resourceUuids.clear();
            logger.debug(String.format("untrack all %s", getResourceName()));
        }
    }

    public void untrack(String resUuid) {
        synchronized (resourceUuids) {
            resourceUuids.remove(resUuid);
            untrackHook(resUuid);
            logger.debug(String.format("stop tracking %s[uuid:%s]", getResourceName(), resUuid));
        }
    }

    public void track(Collection<String> resUuids) {
        synchronized (resourceUuids) {
            for (String resUuid : resUuids) {
                if (!resourceUuids.contains(resUuid)) {
                    resourceUuids.add(resUuid);
                    trackHook(resUuid);
                    logger.debug(String.format("start tracking %s[uuid:%s]", getResourceName(), resUuid));
                }
            }
        }
    }

    public void untrack(Collection<String> resUuids) {
        synchronized (resourceUuids) {
            for (String resUuid : resUuids) {
                resourceUuids.remove(resUuid);
                untrackHook(resUuid);
                logger.debug(String.format("stop tracking %s[uuid:%s]", getResourceName(), resUuid));
            }
        }
    }

    protected void startTracker() {
        if (trackerThread != null) {
            trackerThread.cancel(true);
        }

        if (CoreGlobalProperty.UNIT_TEST_ON) {
            trackerThread = thdf.submitPeriodicTask(new Tracker(), getPingInterval());
        } else {
            trackerThread = thdf.submitPeriodicTask(new Tracker(), getPingInterval() + new Random().nextInt(30));
        }
    }

    @Override
    public boolean start() {
        startTracker();
        startHook();
        return true;
    }

    @Override
    public boolean stop() {
        trackerThread.cancel(true);
        return true;
    }
}
