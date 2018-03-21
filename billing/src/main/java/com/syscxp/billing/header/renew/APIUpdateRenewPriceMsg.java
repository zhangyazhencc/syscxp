package com.syscxp.billing.header.renew;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

import java.math.BigDecimal;

@Action(adminOnly = true,services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_RENEW, names = {"update"})
public class APIUpdateRenewPriceMsg extends APIMessage {

    @APIParam(emptyString = false, resourceType = RenewVO.class, checkAccount = true)
    private String uuid;

    @APIParam
    private BigDecimal price;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update RenewVO Price")
                        .resource(uuid, RenewVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
