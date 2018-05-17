package com.syscxp.header.billing.productimpl;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.APIReply;

import java.sql.Timestamp;

public class APIRenewAutoReply extends APIReply {

    private ProductType productType;

    private String productUuid;

    private Timestamp expireDate;

    private boolean temporaryRenew;

    public boolean isTemporaryRenew() {
        return temporaryRenew;
    }

    public void setTemporaryRenew(boolean temporaryRenew) {
        this.temporaryRenew = temporaryRenew;
    }

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
}
