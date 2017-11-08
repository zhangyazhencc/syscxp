package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

public class APIAttachResourcesByPoliciesMsg extends APIMessage {

    @APIParam
    private List<String> policyUuids;

    @APIParam
    private List<String> resourceUuids;

    @APIParam
    private ProductType type;

    @APIParam
    private String accountUuid;

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

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
