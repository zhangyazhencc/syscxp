package com.syscxp.core.notification;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.config.GlobalConfigException;
import com.syscxp.core.config.GlobalConfigValidatorExtensionPoint;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SQL;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.thread.AsyncThread;
import com.syscxp.core.thread.Task;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ResourceHavingAccountReference;
import com.syscxp.header.core.ExceptionSafe;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.message.*;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.notification.ApiNotificationFactory;
import com.syscxp.header.notification.ApiNotificationFactoryExtensionPoint;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by xing5 on 2017/3/15.
 */
public class NotificationManager extends AbstractService {
    private CLogger logger = Utils.getLogger(getClass());

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private PluginRegistry plugRgty;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ResourceHavingAccountReference resourceHavingAccountReference;

    private Map<Class, ApiNotificationFactory> apiNotificationFactories = new HashMap<>();

    private BlockingQueue<NotificationBuilder> notificationsQueue = new LinkedBlockingQueue<>();
    private NotificationBuilder quitToken = new NotificationBuilder();
    private boolean exitQueue = false;

    private class ApiNotificationSender implements BeforeDeliveryMessageInterceptor, BeforePublishEventInterceptor {
        class Bundle {
            APIMessage message;
            ApiNotification notification;
        }

        ConcurrentHashMap<String, Bundle> apiMessages = new ConcurrentHashMap<>();
        Map<Class, Method> notificationMethods = new ConcurrentHashMap<>();

        public ApiNotificationSender() {
            Set<Method> methods = Platform.getReflections().getMethodsReturn(ApiNotification.class);
            for (Method m : methods) {
                notificationMethods.put(m.getDeclaringClass(), m);
            }
        }

        @Override
        public int orderOfBeforePublishEventInterceptor() {
            return 0;
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
        @AsyncThread
        public void beforePublishEvent(Event evt) {
            if (!(evt instanceof APIEvent)) {
                return;
            }

            APIEvent aevt = (APIEvent) evt;
            Bundle b = apiMessages.get(aevt.getApiId());
            if (b == null) {
                return;
            }

            apiMessages.remove(aevt.getApiId());

            b.notification.after((APIEvent) evt);

            List<NotificationBuilder> lst = new ArrayList<>();
            for (ApiNotification.Inner inner : b.notification.getInners()) {
                Map<String, Object> opaque = new HashMap<>();
                opaque.put("session", b.message.getSession());
                opaque.put("success", aevt.isSuccess());

                if (!aevt.isSuccess()) {
                    opaque.put("error", aevt.getError());
                }

                lst.add(new NotificationBuilder()
                        .content(inner.getContent())
                        .msgfields(JSONObjectUtil.toJsonString(b.message.getDeclaredFieldAndValues()))
                        .category(aevt.getType(bus.getBusProjectId()).toString())
                        .account(inner.getAccountUuid())
                        .action(b.message.getIp(), aevt.isSuccess())
                        .name(b.message.getClass().getSimpleName())
                        .sender(NotificationConstant.API_SENDER)
                        .resource(inner.getResourceUuid(), inner.getResourceClass())
                        .opaque(opaque));
            }

            send(lst);
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

            if (msg instanceof APICreateNotificationMsg) {
                return;
            }

//            if (!msg.getServiceId().endsWith(Platform.getManagementServerId())) {
//                // a message to api portal
//                return;
//            }

            try {
                ApiNotification notification = getApiNotification((APIMessage) msg);

                if (notification == null) {
                    logger.warn(String.format("API message[%s] does not have an API notification method or the method returns null",
                            msg.getClass()));
                    return;
                }

                notification.before();

                Bundle b = new Bundle();
                b.message = (APIMessage) msg;
                b.notification = notification;
                apiMessages.put(msg.getId(), b);
            } catch (Throwable t) {
                logger.warn(String.format("unhandled exception %s", t.getMessage()), t);
            }
        }
    }

    private ApiNotificationSender apiNotificationSender = new ApiNotificationSender();

    void send(List<NotificationBuilder> builders) {
        for (NotificationBuilder builder : builders) {
            send(builder);
        }
    }

    void send(NotificationBuilder builder) {
        try {
            notificationsQueue.offer(builder, 60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn(String.format("unable to write log %s", JSONObjectUtil.toJsonString(builder)), e);
        }
    }

    @Override
    public boolean start() {
        bus.installBeforeDeliveryMessageInterceptor(apiNotificationSender);
        bus.installBeforePublishEventInterceptor(apiNotificationSender);

        for (ApiNotificationFactoryExtensionPoint ext : plugRgty.getExtensionList(ApiNotificationFactoryExtensionPoint.class)) {
            apiNotificationFactories.putAll(ext.apiNotificationFactory());
        }


        NotificationGlobalConfig.WEBHOOK_URL.installValidateExtension((category, name, oldValue, newValue) -> {
            if (newValue == null || "null".equals(newValue)) {
                return;
            }

            if (!new UrlValidator().isValid(newValue)) {
                throw new OperationFailureException(Platform.argerr("%s is not a valid URL", newValue));
            }
        });

        thdf.submit(new Task<Void>() {
            @Override
            public Void call() throws Exception {
                writeNotificationsToDb();
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
    private void writeNotificationsToDb() throws InterruptedException {
        while (!exitQueue) {
            List<NotificationBuilder> lst = new ArrayList<>();
            lst.add(notificationsQueue.take());
            notificationsQueue.drainTo(lst);

            try {
                for (NotificationBuilder builder : lst) {
                    if (builder == quitToken) {
                        exitQueue = true;
                        continue;
                    }

                    SessionInventory session = (SessionInventory) builder.opaque.get("session");

                    APICreateNotificationMsg msg = new APICreateNotificationMsg();

                    if (session != null) {
                        msg.setOpAccountUuid(session.getAccountUuid());
                        msg.setOpUserUuid(session.getUserUuid());
                    }

                    msg.setName(builder.notificationName);
                    msg.setCategory(builder.category);
                    msg.setSuccess(builder.success);
                    msg.setRemoteIp(builder.remoteIp);
                    msg.setMsgfields(builder.msgfields);
                    msg.setContent(builder.content);
                    msg.setResourceType(builder.resourceType);
                    msg.setResourceUuid(builder.resourceUuid);
                    msg.setSender(builder.sender);
                    msg.setType(builder.type);
                    msg.setOpaque(builder.opaque);

                    msg.setAccountUuid(builder.accountUuid);
                    if (msg.getAccountUuid() == null && msg.getResourceType() != null && msg.getResourceUuid() != null) {
                        if (resourceHavingAccountReference.isResourceHavingAccountReference(builder.resourceClass)) {
                            String sql = String.format("select accountUuid from %s where uuid = :resourceUuid ", msg.getResourceType());
                            String accountUuid = SQL.New(sql, String.class).param("resourceUuid", msg.getResourceUuid()).find();
                            if (accountUuid != null)
                                msg.setAccountUuid(accountUuid);
                        }
                    }

                    InnerMessageHelper.setMD5(msg);

                    if (NotificationGlobalConfig.WEBHOOK_URL.value() != null && !"".equals(NotificationGlobalConfig.WEBHOOK_URL.value()) && !NotificationGlobalConfig.WEBHOOK_URL.value().equals("null")) {
                        callWebhook(msg);

                    } else {
                        saveNotificationVO(msg);
                    }
                }

            } catch (Throwable t) {
                logger.warn(String.format("failed to persists notifications:\n %s", JSONObjectUtil.toJsonString(lst)), t);
            }
        }
    }

    @AsyncThread
    private void callWebhook(APIMessage msg) {

        RestAPIResponse rsp = restf.syncJsonPost(NotificationGlobalConfig.WEBHOOK_URL.value(), RESTApiDecoder.dump(msg), RestAPIResponse.class);

        APIReply apiReply = (APIReply) RESTApiDecoder.loads(rsp.getResult());
        if (!apiReply.isSuccess()){
            logger.debug(String.format("Message[%s]:保存日志失败", msg.getClass().getSimpleName()));
        }
    }

    @Override
    public boolean stop() {
        notificationsQueue.offer(quitToken);

        return true;
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private NotificationVO saveNotificationVO(APICreateNotificationMsg msg) {
        NotificationVO vo = new NotificationVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setOpAccountUuid(msg.getOpAccountUuid());
        vo.setOpUserUuid(msg.getOpUserUuid());
        vo.setCategory(msg.getCategory());
        vo.setSuccess(msg.getSuccess());
        vo.setRemoteIp(msg.getRemoteIp());
        vo.setMsgfields(msg.getMsgfields());
        vo.setContent(msg.getContent());
        vo.setResourceType(msg.getResourceType());
        vo.setResourceUuid(msg.getResourceUuid());
        vo.setSender(msg.getSender());
        vo.setStatus(NotificationStatus.Unread);
        vo.setType(msg.getType());
        vo.setTime(System.currentTimeMillis());
        vo.setOpaque(msg.getOpaque());

        return dbf.persistAndRefresh(vo);
    }

    private void handle(APICreateNotificationMsg msg) {
        NotificationVO vo = saveNotificationVO(msg);

        APICreateNotificationsEvent evt = new APICreateNotificationsEvent(msg.getId());
        NotificationInventory inv = NotificationInventory.valueOf(vo);
        evt.setInventory(inv);
        bus.publish(evt);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APIUpdateNotificationsStatusMsg) {
            handle((APIUpdateNotificationsStatusMsg) msg);
        } else if (msg instanceof APIDeleteNotificationsMsg) {
            handle((APIDeleteNotificationsMsg) msg);
        } else if (msg instanceof APICreateNotificationMsg) {
            handle((APICreateNotificationMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIDeleteNotificationsMsg msg) {
        APIDeleteNotificationsEvent evt = new APIDeleteNotificationsEvent(msg.getId());
        SQL.New(NotificationVO.class).in(NotificationVO_.uuid, msg.getUuids()).delete();
        bus.publish(evt);
    }

    private void handle(APIUpdateNotificationsStatusMsg msg) {
        APIUpdateNotificationsStatusEvent evt = new APIUpdateNotificationsStatusEvent(msg.getId());

        SQL.New(NotificationVO.class).set(NotificationVO_.status, NotificationStatus.valueOf(msg.getStatus()))
                .in(NotificationVO_.uuid, msg.getUuids()).update();

        bus.publish(evt);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(NotificationConstant.SERVICE_ID);
    }

}
