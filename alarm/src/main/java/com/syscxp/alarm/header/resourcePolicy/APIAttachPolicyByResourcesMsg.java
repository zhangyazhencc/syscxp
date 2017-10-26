package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

@Action(adminOnly = true,category = AlarmConstant.ACTION_CATEGORY_RESOURCE_POLICY)
public class APIAttachPolicyByResourcesMsg extends APIMessage{

    @APIParam(emptyString = false,resourceType = PolicyVO.class)
    private String policyUuid;

    @APIParam(nonempty = false)
    private List<String> resourceUuids;

    @APIParam
    private boolean isAttach;

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public List<String> getResourceUuids() {
        return resourceUuids;
    }

    public void setResourceUuids(List<String> resourceUuids) {
        this.resourceUuids = resourceUuids;
    }

    public boolean isAttach() {
        return isAttach;
    }

    public void setAttach(boolean attach) {
        isAttach = attach;
    }
}
