package com.syscxp.core.config;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

public class APIUpdateGlobalConfigMsg extends APIMessage {
    @APIParam
    private String category;
    @APIParam
    private String name;
    @APIParam(required = false)
    private String value;

    public String getIdentity() {
        return GlobalConfig.produceIdentity(category, name);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static APIUpdateGlobalConfigMsg __example__() {
        APIUpdateGlobalConfigMsg msg = new APIUpdateGlobalConfigMsg();
        msg.setCategory("quota");
        msg.setName("scheduler.num");
        msg.setValue("90");
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;
        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy(String.format("Updating a global configuration[category:%s, name:%s]", category, name))
                        .resource(null, GlobalConfigVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
