package com.syscxp.billing.header.price;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.Category;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_PRICE)
public class APICreateECPProductPriceUnitMsg extends APIMessage{
    @APIParam(maxLength = 256,emptyString = false)
    private String productCategoryUuid;

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

    public String getProductCategoryUuid() {
        return productCategoryUuid;
    }

    public void setProductCategoryUuid(String productCategoryUuid) {
        this.productCategoryUuid = productCategoryUuid;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
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

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }
}