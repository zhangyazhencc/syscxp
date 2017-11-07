package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

public class APIDetachPolicyByResourcesMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = PolicyVO.class)
    private String policyUuid;

    @APIParam(nonempty = false)
    private List<String> resourceUuids;

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
}
