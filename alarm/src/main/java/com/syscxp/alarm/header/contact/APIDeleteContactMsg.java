package com.syscxp.alarm.header.contact;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(adminOnly = true,category = AlarmConstant.ACTION_CATEGORY_CONTACT)
public class APIDeleteContactMsg extends APIMessage{

    @APIParam(emptyString = false,resourceType=ContactVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
