package com.syscxp.billing.header.price;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import java.util.Map;

@Action(category = BillingConstant.ACTION_CATEGORY_PRICE)
public class APICreateTunnelProductPriceUnitMsg extends APIMessage{
    @APIParam(maxLength = 50)
    private String areaCode;

    @APIParam(maxLength = 50)
    private String productCategoryUuid;

    @APIParam(maxLength = 256)
    private String areaName;

    @APIParam(maxLength = 256)
    private String lineName;

    @APIParam(maxLength = 256)
    private Map<String,Integer> configPrice;

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

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public Map<String, Integer> getConfigPrice() {
        return configPrice;
    }

    public void setConfigPrice(Map<String, Integer> configPrice) {
        this.configPrice = configPrice;
    }

    public String getProductCategoryUuid() {
        return productCategoryUuid;
    }

    public void setProductCategoryUuid(String productCategoryUuid) {
        this.productCategoryUuid = productCategoryUuid;
    }
}
