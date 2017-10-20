package com.syscxp.alarm.header.resourcePolicy;


public class PolicyBindResource {

    private String policyUuid;
    private Long bindingResources;
    public PolicyBindResource(){}

    public PolicyBindResource(Object[] objs) {
        policyUuid = (String) objs[0];
        bindingResources = (Long) objs[1];
    }

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public Long getBindingResources() {
        return bindingResources;
    }

    public void setBindingResources(Long bindingResources) {
        this.bindingResources = bindingResources;
    }
}
