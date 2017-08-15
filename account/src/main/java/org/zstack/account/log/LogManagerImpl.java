package org.zstack.account.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.zstack.account.header.log.OperLogVO;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.CloudBusEventListener;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.notification.NotificationManager;
import org.zstack.core.thread.AsyncThread;
import org.zstack.core.thread.Task;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.GlobalApiMessageInterceptor;
import org.zstack.header.core.ExceptionSafe;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.message.*;
import org.zstack.header.notification.ApiNotification;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class LogManagerImpl extends AbstractService implements LogManager, CloudBusEventListener, GlobalApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(LogManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private EventFacade evtf;

    private Map<String, SessionInventory> sessions = new ConcurrentHashMap<>();

    private BlockingQueue<OperLogBuilder> operLogsQueue = new LinkedBlockingQueue<>();
    private OperLogBuilder quitToken = new OperLogBuilder();
    private boolean exitQueue = false;

    private class ApiOperLogSender implements BeforeDeliveryMessageInterceptor, BeforePublishEventInterceptor {


        ConcurrentHashMap<String, APIMessage> apiMessages = new ConcurrentHashMap<>();


        @Override
        public int orderOfBeforeDeliveryMessageInterceptor() {
            return 0;
        }

        @Override
        public void intercept(Message msg) {
            if (!(msg instanceof APIMessage)) {
                return;
            }

            if (msg instanceof APISyncCallMessage) {
                return;
            }

            if (msg.getServiceId().endsWith(Platform.getManagementServerId())) {
                return;
            }

            try {
                OperLogVO operLogVO = getOperLog((APIMessage) msg);

                apiMessages.put(msg.getId(), (APIMessage) msg);

            } catch (Throwable t) {
                logger.warn(String.format("unhandled exception %s", t.getMessage()), t);
            }
        }

        private OperLogVO getOperLog(APIMessage msg) {
            OperLogVO operLogVO = new OperLogVO();




            return operLogVO;
        }

        @Override
        public int orderOfBeforePublishEventInterceptor() {
            return 0;
        }

        @Override
        @AsyncThread
        public void beforePublishEvent(Event evt) {
            if (!(evt instanceof APIEvent)) {
                return;
            }

            APIEvent aevt = (APIEvent) evt;
            APIMessage msg = apiMessages.get(aevt.getApiId());
            if (msg == null) return;
            apiMessages.remove(aevt.getApiId());



            OperLogBuilder builder = new OperLogBuilder();
            builder.account(msg.getSession().getAccountUuid())
                .user(msg.getSession().getUserUuid())
                .resource("reaourceuuid", aevt.getClass().getName())
                .category("")
                .action("")
                .state(aevt.isSuccess())
                .description("desc");


            send(builder);
        }
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {

    }

    public void send(List<OperLogBuilder> builders) {
        for (OperLogBuilder builder : builders) {
            send(builder);
        }
    }

    public void send(OperLogBuilder builder) {
        try {
            operLogsQueue.offer(builder, 60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn(String.format("unable to write log %s", JSONObjectUtil.toJsonString(builder)), e);
        }
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(LogConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {

        thdf.submit(new Task<Void>() {
            @Override
            public Void call() throws Exception {
                saveOperLogToDb();
                return null;
            }

            @Override
            public String getName() {
                return "notification-thread";
            }
        });
        return true;
    }

    @ExceptionSafe
    private void saveOperLogToDb() throws InterruptedException {
        while (!exitQueue) {
            List<OperLogBuilder> lst = new ArrayList<>();
            lst.add(operLogsQueue.take());
            operLogsQueue.drainTo(lst);

            try{
                for(OperLogBuilder builder:lst){
                    if (builder == quitToken) {
                        exitQueue = true;
                        continue;
                    }
                    OperLogVO operLogVO = new OperLogVO();
                    operLogVO.setUuid(Platform.getUuid());
                    operLogVO.setAccountUuid(builder.accountUuid);
                    operLogVO.setUserUuid(builder.userUuid);
                    operLogVO.setResourceUuid(builder.resourceUuid);
                    operLogVO.setResourceType(builder.resourceType);
                    operLogVO.setAction(builder.action);
                    operLogVO.setCategory(builder.category);
                    operLogVO.setState(builder.state);
                    operLogVO.setDescription(builder.description);

                    dbf.persistAndRefresh(operLogVO);
                }
            }catch (Throwable t) {
                logger.warn(String.format("failed to persists operlogs:\n %s", JSONObjectUtil.toJsonString(lst)), t);
            }


        }

    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        sessions.put(msg.getId(), msg.getSession());
        return msg;
    }

    @Override
    public boolean handleEvent(Event e) {
        try {
            if (!(e instanceof APIEvent)) {
                return false;
            }

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return false;
    }


    @Override
    public List<Class> getMessageClassToIntercept() {
        return null;
    }

    @Override
    public InterceptorPosition getPosition() {
        return InterceptorPosition.END;
    }
}
