package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetResourceUuidsByPolicyReply extends APIReply {

    private List<String> resourceUuids;

    public List<String> getResourceUuids() {
        return resourceUuids;
    }

    public void setResourceUuids(List<String> resourceUuids) {
        this.resourceUuids = resourceUuids;
    }
}
