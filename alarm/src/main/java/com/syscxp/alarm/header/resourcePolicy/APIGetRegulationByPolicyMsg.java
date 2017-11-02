package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@Action(category = AlarmConstant.ACTION_CATEGORY_RESOURCE_POLICY)
public class APIGetRegulationByPolicyMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,resourceType = PolicyVO.class)
    private String policyUuid;

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }
}
