package com.syscxp.alarm.header.contact;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {AlarmConstant.ACTION_SERVICE}, category = AlarmConstant.ACTION_CATEGORY_ALARM, names = {"delete"})
public class APIDeleteContactMsg extends APIMessage{

    @APIParam(emptyString = false, resourceType=ContactVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Delete ContactVO")
                        .resource(uuid, ContactVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
