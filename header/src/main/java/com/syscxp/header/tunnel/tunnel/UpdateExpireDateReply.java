package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.MessageReply;

/**
 * Create by DCY on 2018/5/17
 */
public class UpdateExpireDateReply extends MessageReply {

    private ProductType productType;

    private String productUuid;

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
}
