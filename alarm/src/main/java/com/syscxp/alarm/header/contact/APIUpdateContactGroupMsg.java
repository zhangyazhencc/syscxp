package com.syscxp.alarm.header.contact;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = AlarmConstant.ACTION_CATEGORY_CONTACT)
public class APIUpdateContactGroupMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = ContactGroupVO.class)
    private String uuid;

    @APIParam(required = false,emptyString = false)
    private String groupCode;

    @APIParam(required = false,emptyString = false)
    private String groupName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
