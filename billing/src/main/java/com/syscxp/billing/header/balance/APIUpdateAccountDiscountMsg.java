package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.AccountDiscountVO;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_DISCOUNT, names = {"update"})
public class APIUpdateAccountDiscountMsg extends APIMessage{

    @APIParam(emptyString = false, resourceType = AccountDiscountVO.class)
    private String uuid;

    @APIParam(numberRange = {1, 100})
    private int discount;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update AccountDiscountVO")
                        .resource(uuid, AccountDiscountVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
