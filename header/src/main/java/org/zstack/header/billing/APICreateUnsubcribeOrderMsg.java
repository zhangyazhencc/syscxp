package org.zstack.header.billing;

import org.zstack.header.message.APIParam;

public class APICreateUnsubcribeOrderMsg extends APICreateOrderMsg {

    @APIParam(emptyString = false)
    private String productName;

    @APIParam(emptyString = false)
    private ProductType productType;

    @APIParam(emptyString = false)
    private String productUuid;

    @APIParam
    private String productDescription;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}