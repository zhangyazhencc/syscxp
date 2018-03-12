package com.syscxp.header.billing;

public class ProductPriceUnit {

    private ProductType productTypeCode;

    private ProductCategory categoryCode;

    private String areaCode = "DEFAULT";

    private String lineCode = "DEFAULT";

    private String configCode = "DEFAULT";

    public ProductType getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(ProductType productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    public ProductCategory getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(ProductCategory categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }
}
