package com.syscxp.alarm.header.resourcePolicy;


import java.math.BigInteger;

public class PolicyBindResource {

    private String policyUuid;
    private BigInteger bindingResources;
    public PolicyBindResource(){}

    public PolicyBindResource(Object[] objs) {
        policyUuid = (String) objs[0];
        bindingResources = (BigInteger) objs[1];
    }

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public BigInteger getBindingResources() {
        return bindingResources;
    }

    public void setBindingResources(BigInteger bindingResources) {
        this.bindingResources = bindingResources;
    }
}
