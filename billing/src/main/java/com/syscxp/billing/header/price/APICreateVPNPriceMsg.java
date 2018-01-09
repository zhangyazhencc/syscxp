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

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private Integer config2MPrice;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private Integer config5MPrice;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private Integer config10MPrice;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private Integer config20MPrice;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private Integer config50MPrice;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private Integer config100MPrice;

    @APIParam(numberRange = {0,Integer.MAX_VALUE})
    private Integer config200MPrice;

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


    public Integer getConfig2MPrice() {
        return config2MPrice;
    }

    public void setConfig2MPrice(Integer config2MPrice) {
        this.config2MPrice = config2MPrice;
    }

    public Integer getConfig5MPrice() {
        return config5MPrice;
    }

    public void setConfig5MPrice(Integer config5MPrice) {
        this.config5MPrice = config5MPrice;
    }

    public Integer getConfig10MPrice() {
        return config10MPrice;
    }

    public void setConfig10MPrice(Integer config10MPrice) {
        this.config10MPrice = config10MPrice;
    }

    public Integer getConfig20MPrice() {
        return config20MPrice;
    }

    public void setConfig20MPrice(Integer config20MPrice) {
        this.config20MPrice = config20MPrice;
    }

    public Integer getConfig50MPrice() {
        return config50MPrice;
    }

    public void setConfig50MPrice(Integer config50MPrice) {
        this.config50MPrice = config50MPrice;
    }

    public Integer getConfig100MPrice() {
        return config100MPrice;
    }

    public void setConfig100MPrice(Integer config100MPrice) {
        this.config100MPrice = config100MPrice;
    }

    public Integer getConfig200MPrice() {
        return config200MPrice;
    }

    public void setConfig200MPrice(Integer config200MPrice) {
        this.config200MPrice = config200MPrice;
    }
}
