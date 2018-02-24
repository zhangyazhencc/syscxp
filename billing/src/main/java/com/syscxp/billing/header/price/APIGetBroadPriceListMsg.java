package com.syscxp.billing.header.price;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.Category;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_PRICE, names = {"read"})
public class APIGetBroadPriceListMsg extends APISyncCallMessage{

    @APIParam
    private ProductType productType;

    @APIParam
    private Category category;

    @APIParam(required = false,emptyString = false)
    private String areaCode;

    @APIParam
    private int start;

    @APIParam
    private int limit;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

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
}
