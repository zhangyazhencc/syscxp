package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.NeedReplyMessage;

/**
 * Create by DCY on 2018/5/17
 */
public class RenewAutoMsg extends NeedReplyMessage implements LocalTunnelBillingMessage{

    private String uuid;
    private Integer duration;
    private ProductChargeModel productChargeModel;
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

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
