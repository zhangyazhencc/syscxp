package org.zstack.billing.header.order;

import org.zstack.billing.header.balance.ProductType;

public class ProductPriceUnit {

    private String uuid;

    private ProductType productType;

    private Category category;

    private String config;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
