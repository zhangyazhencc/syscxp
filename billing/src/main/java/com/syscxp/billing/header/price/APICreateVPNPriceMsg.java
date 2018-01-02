package com.syscxp.billing.header.price;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.Category;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_PRICE)
public class APICreateVPNPriceMsg extends APIMessage {

    @APIParam(maxLength = 256,emptyString = false)
    private ProductType productType;

    @APIParam(maxLength = 256,emptyString = false)
    private Category category;

    @APIParam( maxLength = 256,emptyString = false)
    private String areaCode;

    @APIParam( maxLength = 256,emptyString = false)
    private String areaName;

    @APIParam( maxLength = 256,emptyString = false)
    private String lineCode;

    @APIParam( maxLength = 256,emptyString = false)
    private String lineName;

    @APIParam(maxLength = 256,emptyString = false)
    private String configCode;

    @APIParam(maxLength = 256,emptyString = false)
    private String configName;

    @APIParam(maxLength = 256,emptyString = false)
    private Integer unitPrice;

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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }
}
