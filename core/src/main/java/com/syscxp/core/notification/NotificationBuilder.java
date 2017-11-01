package com.syscxp.core.notification;

import com.syscxp.core.Platform;

import java.util.*;

/**
 * Created by xing5 on 2017/3/15.
 */
public class NotificationBuilder {
    String notificationName;
    String category;
    String accountUuid;
    String remoteIp;
    Boolean success;
    String content;
    String sender = NotificationConstant.SYSTEM_SENDER;
    String resourceUuid;
    String resourceType;
    Map opaque;
    List arguments = new ArrayList();
    Map msgfields = new HashMap();
    NotificationType type = NotificationType.Info;


    public NotificationBuilder category(String category) {
        this.category = category;
        return this;
    }

    public NotificationBuilder action(String remoteIp, boolean success) {
        this.remoteIp = remoteIp;
        this.success = success;
        return this;
    }

    public NotificationBuilder name(String name) {
        notificationName = name;
        return this;
    }

    public NotificationBuilder ower(String accountUuid) {
        accountUuid = accountUuid;
        return this;
    }

    public NotificationBuilder content(String content) {
        this.content = content;
        return this;
    }

    public NotificationBuilder msgfields(Map msgfields){
        this.msgfields = msgfields;
        return this;
    }

    public NotificationBuilder arguments(Object... args) {
        if (args != null) {
            Collections.addAll(arguments, args);
        }

        return this;
    }

    public NotificationBuilder sender(String sender) {
        this.sender = sender;
        return this;
    }

    public NotificationBuilder opaque(Map opaque) {
        this.opaque = opaque;
        return this;
    }

    public NotificationBuilder resource(String uuid, String type) {
        resourceUuid = uuid;
        resourceType = type;
        return this;
    }

    public NotificationBuilder type(NotificationType type) {
        this.type = type;
        return this;
    }

    public void send() {
        NotificationManager mgr = Platform.getComponentLoader().getComponent(NotificationManager.class);
        mgr.send(this);
    }
}
