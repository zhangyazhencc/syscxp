package com.syscxp.alarm.header.contact;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

@Action(category = AlarmConstant.ACTION_CATEGORY_CONTACT)
public class APIAttachGroupByContactsMsg extends APIMessage{

    @APIParam(emptyString = false,resourceType = ContactGroupVO.class)
    private String groupUuid;

    @APIParam(nonempty = false)
    List<String> contactUuids;

    @APIParam
    private boolean isAttach;

    public boolean isAttach() {
        return isAttach;
    }

    public void setAttach(boolean attach) {
        isAttach = attach;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public List<String> getContactUuids() {
        return contactUuids;
    }

    public void setContactUuids(List<String> contactUuids) {
        this.contactUuids = contactUuids;
    }
}
