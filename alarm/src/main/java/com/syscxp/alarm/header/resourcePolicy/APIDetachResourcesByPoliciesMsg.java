package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

public class APIDetachResourcesByPoliciesMsg extends APIMessage {

    @APIParam
    private String accountUuid;

    @APIParam
    private ProductType type;

    @APIParam
    private List<String> policyUuids;

    @APIParam
    private List<String> resourceUuids;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public List<String> getPolicyUuids() {
        return policyUuids;
    }

    public void setPolicyUuids(List<String> policyUuids) {
        this.policyUuids = policyUuids;
    }

    public List<String> getResourceUuids() {
        return resourceUuids;
    }

    public void setResourceUuids(List<String> resourceUuids) {
        this.resourceUuids = resourceUuids;
    }
}
