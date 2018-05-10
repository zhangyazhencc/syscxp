package com.syscxp.header.alarm;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@InnerCredentialCheck
public class APIStartResourceAlarmMsg extends APISyncCallMessage{

    @APIParam
    private String resourceUuid;

    private ProductType type;

    private String monitorUuid;

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public String getMonitorUuid() {
        return monitorUuid;
    }

    public void setMonitorUuid(String monitorUuid) {
        this.monitorUuid = monitorUuid;
    }
}
