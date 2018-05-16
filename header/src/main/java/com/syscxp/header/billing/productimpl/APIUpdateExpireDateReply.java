package com.syscxp.header.billing.productimpl;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.message.APIReply;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-04-26.
 * @Description: .
 */
public class APIUpdateExpireDateReply extends APIReply {

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
