package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@Action(category = AlarmConstant.ACTION_CATEGORY_RESOURCE_POLICY)
public class APIGetResourcesBindByPolicyMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,resourceType = PolicyVO.class)
    private String policyUuid;

    @APIParam(required = false)
    private String name;

    @APIParam
    private String accountUuid;

    @APIParam
    private boolean isBind;

    @APIParam
    private Integer limit = 1000;

    @APIParam
    private Integer start;

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
