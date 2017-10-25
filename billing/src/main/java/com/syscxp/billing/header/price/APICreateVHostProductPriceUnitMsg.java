package com.syscxp.billing.header.price;

import com.syscxp.header.billing.Category;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

public class APICreateVHostProductPriceUnitMsg extends APIMessage{
    @APIParam(maxLength = 256,emptyString = false)
    private Category categoryCode;

    @APIParam( maxLength = 256,emptyString = false)
    private String categoryName;

    @APIParam( maxLength = 256,emptyString = false)
    private String areaName;

    @APIParam( maxLength = 256,emptyString = false)
    private String lineName;

    @APIParam(maxLength = 256,emptyString = false)
    private String configName;

    @APIParam(maxLength = 256,emptyString = false)
    private Integer unitPrice;


    public Category getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(Category categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
}
