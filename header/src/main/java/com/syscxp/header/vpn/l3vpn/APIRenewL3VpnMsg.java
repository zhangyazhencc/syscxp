package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.vpn.vpn.VpnConstant;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"})
public class APIRenewL3VpnMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, resourceType = L3VpnVO.class, checkAccount = true)
    private String uuid;

    @APIParam
    private Integer duration;

    @APIParam(required = false, validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK", "BY_DAY"})
    private ProductChargeModel productChargeModel;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("L3VpnVO Renew")
                        .resource(uuid, L3VpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
