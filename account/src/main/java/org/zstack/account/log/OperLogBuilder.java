package org.zstack.account.log;

import org.zstack.core.Platform;

public class OperLogBuilder {
    String accountUuid;
    String userUuid;
    String category;
    String action;
    String resourceUuid;
    String resourceType;
    String state;
    String description;

    public OperLogBuilder account(String accountUuid) {
        this.accountUuid = accountUuid;
        return this;
    }

    public OperLogBuilder user(String userUuid) {
        this.userUuid = userUuid;
        return this;
    }

    public OperLogBuilder category(String category) {
        this.category = category;
        return this;
    }

    public OperLogBuilder action(String action) {
        this.action = action;
        return this;
    }

    public OperLogBuilder resource(String uuid, String type) {
        this.resourceUuid = uuid;
        this.resourceType = type;
        return this;
    }

    public OperLogBuilder state(boolean state) {
        this.state = state ? "sucess" : "fail";
        return this;
    }

    public OperLogBuilder description(String description) {
        this.description = description;
        return this;
    }

    public void send() {
        LogManager mgr = Platform.getComponentLoader().getComponent(LogManagerImpl.class);
        mgr.send(this);
    }
}
