package com.syscxp.core.notification;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.message.NeedReplyMessage;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import com.syscxp.utils.DebugUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.Map;

/**
 * Created by xing5 on 2017/5/8.
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class N {
    private static CLogger logger = Utils.getLogger(N.class);

    private NotificationBuilder builder;

    @Autowired
    private NotificationManager mgr;

    @Autowired
    private CloudBus bus;

    public static N New(String resourceType, String resourceUuid) {
        return new N(resourceType, resourceUuid);
    }

    public static N New(Class resourceClass, String resourceUuid) {
        DebugUtils.Assert(resourceClass != null, "resourceClass cannot be null");
        return new N(resourceClass, resourceUuid);
    }

    private N(String resourceType, String resourceUuid) {
        builder = new NotificationBuilder();
        builder.type(resourceUuid, resourceType);
    }

    private N(Class resourceClass, String resourceUuid) {
        builder = new NotificationBuilder();
        builder.type(resourceUuid, resourceClass.getSimpleName());
    }

    private void send() {
        mgr.send(builder);
    }

    public void warn(String content, NeedReplyMessage msg, MessageReply reply) {
        log(content, msg, reply, NotificationType.Warning);
    }

    public void info(String content, NeedReplyMessage msg, MessageReply reply) {
        log(content, msg, reply, NotificationType.Info);
    }

    public void error(String content, NeedReplyMessage msg, MessageReply reply) {
        log(content, msg, reply, NotificationType.Error);
    }

    private void log(String content, NeedReplyMessage msg, MessageReply reply, NotificationType type) {
        builder.content(content)
                .msgfields(JSONObjectUtil.toJsonString(msg.getDeclaredFieldAndValues()))
                .category(NotificationConstant.SYSTEM_SENDER)
                .name(msg.getClass().getSimpleName())
                .sender(bus.getBusProjectId())
                .action(msg.getIp(), reply.isSuccess())
                .type(type);

        send();
    }

    public N opaque(Map o) {
        builder.opaque(o);
        return this;
    }
}
