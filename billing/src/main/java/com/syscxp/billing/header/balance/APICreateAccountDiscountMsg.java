package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.Category;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_BILLING, names = {"create"})
public class APICreateAccountDiscountMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(emptyString = false)
    private ProductType productType;

    @APIParam
    private Category category;

    @APIParam(numberRange = {1,100})
    private int  discount;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
