package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.*;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_BILLING, names = {"create"})
public class APICreateAccountDiscountMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(emptyString = false)
    private ProductType productType;

    @APIParam
    private ProductCategory category;

    @APIParam(numberRange = {1,100})
    private int  discount;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
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

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateAccountDiscountEvent) evt).getInventory().getUuid();
                }
                ntfy("Create AccountDiscountVO")
                        .resource(uuid, AccountDiscountVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
