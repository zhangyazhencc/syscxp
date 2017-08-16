package org.zstack.account.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.log.OperLogVO;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.CloudBusEventListener;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.thread.AsyncThread;
import org.zstack.core.thread.Task;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.GlobalApiMessageInterceptor;
import org.zstack.header.core.ExceptionSafe;
import org.zstack.header.message.*;
import org.zstack.header.notification.ApiNotification;
import org.zstack.header.notification.ApiNotificationFactory;
import org.zstack.header.notification.ApiNotificationFactoryExtensionPoint;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

import java.lang.reflect.InvocationTargetException;
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

    private Map<Class, ApiNotificationFactory> apiNotificationFactories = new HashMap<>();

    private BlockingQueue<OperLogBuilder> operLogsQueue = new LinkedBlockingQueue<>();
    private OperLogBuilder quitToken = new OperLogBuilder();
    private boolean exitQueue = false;

    private class ApiOperLogSender implements BeforeDeliveryMessageInterceptor, BeforePublishEventInterceptor {

        class Bundle{
            APIMessage message;
            ApiNotification notification;
        }

        ConcurrentHashMap<String, LogManagerImpl.ApiOperLogSender.Bundle> apiMessages = new ConcurrentHashMap<>();
        Map<Class, Method> notificationMethods = new ConcurrentHashMap<>();

        public ApiOperLogSender() {
            Set<Method> methods = Platform.getReflections().getMethodsReturn(ApiNotification.class);
            for (Method m : methods) {
                notificationMethods.put(m.getDeclaringClass(), m);
            }
        }

        private ApiNotification getApiNotification(APIMessage msg) throws InvocationTargetException, IllegalAccessException {
            Method m = notificationMethods.get(msg.getClass());
            if (m == null) {
                Class clz = msg.getClass().getSuperclass();
                while (clz != Object.class) {
                    m = notificationMethods.get(clz);
                    if (m != null) {
                        break;
                    }
                    clz = clz.getSuperclass();
                }

                if (m != null) {
                    notificationMethods.put(msg.getClass(), m);
                }
            }

            ApiNotification notification = null;

            if (m != null) {
                notification = (ApiNotification) m.invoke(msg);
            } else {
                ApiNotificationFactory factory = apiNotificationFactories.get(msg.getClass());
                if (factory != null) {
                    notification = factory.createApiNotification(msg);
                }
            }

            return notification;
        }

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
                ApiNotification notification = getApiNotification((APIMessage) msg);

                if (notification == null) {
                    logger.warn(String.format("API message[%s] does not have an API notification method or the method returns null",
                            msg.getClass()));
                    return;
                }

                Bundle b = new Bundle();
                b.message = (APIMessage) msg;
                b.notification = notification;
                apiMessages.put(msg.getId(), b);
            } catch (Throwable t) {
                logger.warn(String.format("unhandled exception %s", t.getMessage()), t);
            }
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
            Bundle b = apiMessages.get(aevt.getApiId());
            if (b == null) return;
            apiMessages.remove(aevt.getApiId());
            b.notification.after((APIEvent) evt);

            List<OperLogBuilder> lst = new ArrayList<>();
            for (ApiNotification.Inner inner : b.notification.getInners()) {

                String action;
                if (aevt.getClass().getSimpleName().toUpperCase().contains(LogConstant.CREATE_ACTION)){
                    action = LogConstant.CREATE_ACTION;
                } else if (aevt.getClass().getSimpleName().toUpperCase().contains(LogConstant.DELETE_ACTION)){
                    action = LogConstant.DELETE_ACTION;
                } else {
                    action = LogConstant.UPDATE_ACTION;
                }

                lst.add(new OperLogBuilder()
                        .account(b.message.getSession().getAccountUuid())
                        .user(b.message.getSession().getUserUuid())
                        .resource(inner.getResourceUuid(), inner.getResourceType())
                        .category(aevt.getType().toString())
                        .action(action)
                        .state(aevt.isSuccess())
                        .description(inner.getContent()));
            }
            send(lst);
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
    private ApiOperLogSender apiOperLogSender = new ApiOperLogSender();

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
        bus.installBeforeDeliveryMessageInterceptor(apiOperLogSender);
        bus.installBeforePublishEventInterceptor(apiOperLogSender);

        for (ApiNotificationFactoryExtensionPoint ext : pluginRgty.getExtensionList(ApiNotificationFactoryExtensionPoint.class)) {
            apiNotificationFactories.putAll(ext.apiNotificationFactory());
        }

        thdf.submit(new Task<Void>() {
            @Override
            public Void call() throws Exception {
                saveOperLogToDb();
                return null;
            }

            @Override
            public String getName() {
                return "log-thread";
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
        operLogsQueue.offer(quitToken);
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
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
