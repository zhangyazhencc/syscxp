package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2018/5/17
 */
public class SlaMsg extends NeedReplyMessage implements LocalTunnelBillingMessage{

    private String uuid;
    private Integer duration;
    private String slaUuid;
    private ProductType productType;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getSlaUuid() {
        return slaUuid;
    }

    public void setSlaUuid(String slaUuid) {
        this.slaUuid = slaUuid;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
