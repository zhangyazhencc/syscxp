package com.syscxp.billing.header.price;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.ProductCategory;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_PRICE, adminOnly = true)
public class APICreateSharePortPriceMsg extends APIMessage {
    @APIParam(maxLength = 256,emptyString = false)
    private ProductType productType;

    @APIParam(maxLength = 256,emptyString = false)
    private ProductCategory category;

    @APIParam( maxLength = 256,emptyString = false)
    private String areaCode;

    @APIParam( maxLength = 256,emptyString = false)
    private String areaName;

    @APIParam( maxLength = 256,emptyString = false)
    private String lineCode;

    @APIParam( maxLength = 256,emptyString = false)
    private String lineName;

    @APIParam( maxLength = 256,emptyString = false)
    private String configLT500MCode;

    @APIParam( maxLength = 256,emptyString = false)
    private String configGT500MLT2GCode;

    @APIParam( maxLength = 256,emptyString = false)
    private String configGT2GCode;

    @APIParam( maxLength = 256,emptyString = false)
    private String configLT500MName;

    @APIParam( maxLength = 256,emptyString = false)
    private String configGT500MLT2GName;

    @APIParam( maxLength = 256,emptyString = false)
    private String configGT2GName;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private Integer configLT500MPrice;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private Integer configGT500MLT2GPrice;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private Integer configGT2GPrice;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
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

    public String getConfigLT500MCode() {
        return configLT500MCode;
    }

    public void setConfigLT500MCode(String configLT500MCode) {
        this.configLT500MCode = configLT500MCode;
    }

    public String getConfigGT500MLT2GCode() {
        return configGT500MLT2GCode;
    }

    public void setConfigGT500MLT2GCode(String configGT500MLT2GCode) {
        this.configGT500MLT2GCode = configGT500MLT2GCode;
    }

    public String getConfigGT2GCode() {
        return configGT2GCode;
    }

    public void setConfigGT2GCode(String configGT2GCode) {
        this.configGT2GCode = configGT2GCode;
    }

    public String getConfigLT500MName() {
        return configLT500MName;
    }

    public void setConfigLT500MName(String configLT500MName) {
        this.configLT500MName = configLT500MName;
    }

    public String getConfigGT500MLT2GName() {
        return configGT500MLT2GName;
    }

    public void setConfigGT500MLT2GName(String configGT500MLT2GName) {
        this.configGT500MLT2GName = configGT500MLT2GName;
    }

    public String getConfigGT2GName() {
        return configGT2GName;
    }

    public void setConfigGT2GName(String configGT2GName) {
        this.configGT2GName = configGT2GName;
    }

    public Integer getConfigLT500MPrice() {
        return configLT500MPrice;
    }

    public void setConfigLT500MPrice(Integer configLT500MPrice) {
        this.configLT500MPrice = configLT500MPrice;
    }

    public Integer getConfigGT500MLT2GPrice() {
        return configGT500MLT2GPrice;
    }

    public void setConfigGT500MLT2GPrice(Integer configGT500MLT2GPrice) {
        this.configGT500MLT2GPrice = configGT500MLT2GPrice;
    }

    public Integer getConfigGT2GPrice() {
        return configGT2GPrice;
    }

    public void setConfigGT2GPrice(Integer configGT2GPrice) {
        this.configGT2GPrice = configGT2GPrice;
    }
}
