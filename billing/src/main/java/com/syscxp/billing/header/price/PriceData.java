package com.syscxp.billing.header.price;

import com.syscxp.header.billing.Category;
import com.syscxp.header.billing.ProductType;

import java.math.BigDecimal;

public class PriceData {

    private String areaCode;
    private String areaName;
    private String lineCode;
    private String lineName;
    private String configMixPrice;
    private BigDecimal config2MPrice;
    private BigDecimal config5MPrice;
    private BigDecimal config10MPrice;
    private BigDecimal config20MPrice;
    private BigDecimal config50MPrice;
    private BigDecimal config100MPrice;
    private BigDecimal config200MPrice;
    private BigDecimal config500MPrice;
    private BigDecimal config1GPrice;
    private BigDecimal config2GPrice;
    private BigDecimal config5GPrice;
    private BigDecimal config10GPrice;

    public PriceData(){ }
    public PriceData(Object[] objs) {
        areaCode = (String) objs[0];
        areaName = (String) objs[1];
        lineCode = (String) objs[2];
        lineName = (String) objs[3];
        configMixPrice = (String) objs[4];
    }

    public String getConfigMixPrice() {
        return configMixPrice;
    }

    public void setConfigMixPrice(String configMixPrice) {
        this.configMixPrice = configMixPrice;
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

    public BigDecimal getConfig2MPrice() {
        return config2MPrice;
    }

    public void setConfig2MPrice(BigDecimal config2MPrice) {
        this.config2MPrice = config2MPrice;
    }

    public BigDecimal getConfig5MPrice() {
        return config5MPrice;
    }

    public void setConfig5MPrice(BigDecimal config5MPrice) {
        this.config5MPrice = config5MPrice;
    }

    public BigDecimal getConfig10MPrice() {
        return config10MPrice;
    }

    public void setConfig10MPrice(BigDecimal config10MPrice) {
        this.config10MPrice = config10MPrice;
    }

    public BigDecimal getConfig20MPrice() {
        return config20MPrice;
    }

    public void setConfig20MPrice(BigDecimal config20MPrice) {
        this.config20MPrice = config20MPrice;
    }

    public BigDecimal getConfig50MPrice() {
        return config50MPrice;
    }

    public void setConfig50MPrice(BigDecimal config50MPrice) {
        this.config50MPrice = config50MPrice;
    }

    public BigDecimal getConfig100MPrice() {
        return config100MPrice;
    }

    public void setConfig100MPrice(BigDecimal config100MPrice) {
        this.config100MPrice = config100MPrice;
    }

    public BigDecimal getConfig200MPrice() {
        return config200MPrice;
    }

    public void setConfig200MPrice(BigDecimal config200MPrice) {
        this.config200MPrice = config200MPrice;
    }

    public BigDecimal getConfig500MPrice() {
        return config500MPrice;
    }

    public void setConfig500MPrice(BigDecimal config500MPrice) {
        this.config500MPrice = config500MPrice;
    }

    public BigDecimal getConfig1GPrice() {
        return config1GPrice;
    }

    public void setConfig1GPrice(BigDecimal config1GPrice) {
        this.config1GPrice = config1GPrice;
    }

    public BigDecimal getConfig2GPrice() {
        return config2GPrice;
    }

    public void setConfig2GPrice(BigDecimal config2GPrice) {
        this.config2GPrice = config2GPrice;
    }

    public BigDecimal getConfig5GPrice() {
        return config5GPrice;
    }

    public void setConfig5GPrice(BigDecimal config5GPrice) {
        this.config5GPrice = config5GPrice;
    }

    public BigDecimal getConfig10GPrice() {
        return config10GPrice;
    }

    public void setConfig10GPrice(BigDecimal config10GPrice) {
        this.config10GPrice = config10GPrice;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
