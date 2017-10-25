package com.syscxp.alarm.header.contact;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

@Action(category = AlarmConstant.ACTION_CATEGORY_CONTACT)
public class APIAttachContactByGroupsMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = ContactVO.class)
    private String contactUuid;

    @APIParam(nonempty = false)
    private List<String> groupUuids;

    @APIParam
    private boolean isAttach;

    public String getContactUuid() {
        return contactUuid;
    }

    public void setContactUuid(String contactUuid) {
        this.contactUuid = contactUuid;
    }

    public List<String> getGroupUuids() {
        return groupUuids;
    }

    public void setGroupUuids(List<String> groupUuids) {
        this.groupUuids = groupUuids;
    }

    public boolean isAttach() {
        return isAttach;
    }

    public void setAttach(boolean attach) {
        isAttach = attach;
    }
}
