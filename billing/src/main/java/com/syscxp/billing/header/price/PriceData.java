package com.syscxp.billing.header.price;

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
    private BigDecimal config300MPrice;
    private BigDecimal config400MPrice;
    private BigDecimal config500MPrice;
    private BigDecimal config1GPrice;
    private BigDecimal config2GPrice;
    private BigDecimal config3GPrice;
    private BigDecimal config4GPrice;
    private BigDecimal config5GPrice;
    private BigDecimal config6GPrice;
    private BigDecimal config10GPrice;
    private BigDecimal configLT500MPrice;
    private BigDecimal configGT500MLT2GPrice;
    private BigDecimal configGT2GPrice;
    private BigDecimal config20GPrice;

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

    public BigDecimal getConfigLT500MPrice() {
        return configLT500MPrice;
    }

    public void setConfigLT500MPrice(BigDecimal configLT500MPrice) {
        this.configLT500MPrice = configLT500MPrice;
    }

    public BigDecimal getConfigGT500MLT2GPrice() {
        return configGT500MLT2GPrice;
    }

    public void setConfigGT500MLT2GPrice(BigDecimal configGT500MLT2GPrice) {
        this.configGT500MLT2GPrice = configGT500MLT2GPrice;
    }

    public BigDecimal getConfigGT2GPrice() {
        return configGT2GPrice;
    }

    public void setConfigGT2GPrice(BigDecimal configGT2GPrice) {
        this.configGT2GPrice = configGT2GPrice;
    }

    public BigDecimal getConfig300MPrice() {
        return config300MPrice;
    }

    public void setConfig300MPrice(BigDecimal config300MPrice) {
        this.config300MPrice = config300MPrice;
    }

    public BigDecimal getConfig400MPrice() {
        return config400MPrice;
    }

    public void setConfig400MPrice(BigDecimal config400MPrice) {
        this.config400MPrice = config400MPrice;
    }

    public BigDecimal getConfig20GPrice() {
        return config20GPrice;
    }

    public void setConfig20GPrice(BigDecimal config20GPrice) {
        this.config20GPrice = config20GPrice;
    }

    public BigDecimal getConfig3GPrice() {
        return config3GPrice;
    }

    public void setConfig3GPrice(BigDecimal config3GPrice) {
        this.config3GPrice = config3GPrice;
    }

    public BigDecimal getConfig4GPrice() {
        return config4GPrice;
    }

    public void setConfig4GPrice(BigDecimal config4GPrice) {
        this.config4GPrice = config4GPrice;
    }

    public BigDecimal getConfig6GPrice() {
        return config6GPrice;
    }

    public void setConfig6GPrice(BigDecimal config6GPrice) {
        this.config6GPrice = config6GPrice;
    }
}
