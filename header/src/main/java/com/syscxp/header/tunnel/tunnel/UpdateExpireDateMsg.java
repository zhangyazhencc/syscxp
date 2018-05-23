package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.NeedReplyMessage;

import java.sql.Timestamp;

/**
 * Create by DCY on 2018/5/17
 */
public class UpdateExpireDateMsg extends NeedReplyMessage implements LocalTunnelBillingMessage{
    private String uuid;
    private Timestamp expireDate;
    private ProductType productType;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
