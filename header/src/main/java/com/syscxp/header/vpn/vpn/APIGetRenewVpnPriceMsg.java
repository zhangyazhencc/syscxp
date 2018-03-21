package com.syscxp.header.vpn.vpn;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIGetRenewVpnPriceMsg extends APISyncCallMessage {
    @APIParam(emptyString = false, resourceType = VpnVO.class, checkAccount = true)
    private String uuid;
    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK", "BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private int duration;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ProductChargeModel getProductChargeModel() {

        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }
}
