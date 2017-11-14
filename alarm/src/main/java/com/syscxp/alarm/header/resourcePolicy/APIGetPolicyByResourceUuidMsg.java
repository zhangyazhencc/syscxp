package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.List;

@Action(category = AlarmConstant.ACTION_CATEGORY_RESOURCE_POLICY)
public class APIGetPolicyByResourceUuidMsg  extends APISyncCallMessage{
    @APIParam
    private List<String> resourceUuids;

    public List<String> getResourceUuids() {
        return resourceUuids;
    }

    public void setResourceUuids(List<String> resourceUuids) {
        this.resourceUuids = resourceUuids;
    }
}
