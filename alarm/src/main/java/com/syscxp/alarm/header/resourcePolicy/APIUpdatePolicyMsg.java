package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {AlarmConstant.ACTION_SERVICE}, category = AlarmConstant.ACTION_CATEGORY_ALARM, names = {"update"})
public class APIUpdatePolicyMsg extends APIMessage{

    @APIParam(emptyString = false,resourceType = PolicyVO.class)
    private String uuid;

    @APIParam(required = false)
    private String name;

    @APIParam(required = false)
    private String description;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update Alarm PolicyVO")
                        .resource(uuid, PolicyVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
