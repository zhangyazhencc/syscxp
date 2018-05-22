package com.syscxp.header.alarm;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.List;

@InnerCredentialCheck
public class APIStartResourceAlarmMsg extends APISyncCallMessage{

    @APIParam
    private String resourceUuid;

    @APIParam
    private ProductType productType;

    @APIParam
    private List<String> monitorUuids;

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public List<String> getMonitorUuids() {
        return monitorUuids;
    }

    public void setMonitorUuids(List<String> monitorUuids) {
        this.monitorUuids = monitorUuids;
    }
}
