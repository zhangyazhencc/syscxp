package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.MessageReply;

import java.sql.Timestamp;

/**
 * Create by DCY on 2018/5/17
 */
public class RenewAutoReply extends MessageReply {
    private ProductType productType;

    private String productUuid;

    private Timestamp expireDate;

    private boolean temporaryRenew;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }

    public boolean isTemporaryRenew() {
        return temporaryRenew;
    }

    public void setTemporaryRenew(boolean temporaryRenew) {
        this.temporaryRenew = temporaryRenew;
    }
}
