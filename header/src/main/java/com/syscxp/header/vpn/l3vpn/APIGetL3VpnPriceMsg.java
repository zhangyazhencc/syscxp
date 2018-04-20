package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.vpn.vpn.VpnConstant;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIGetL3VpnPriceMsg extends APISyncCallMessage {
    @APIParam
    private String bandwidthOfferingUuid;
    @APIParam(numberRange = {1, Integer.MAX_VALUE})
    private int duration;

    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK", "BY_DAY"})
    private ProductChargeModel productChargeModel;

    @APIParam(emptyString = false)
    private String EndpointUuid;

    @APIParam(required = false)
    private String accountUuid;

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getAccountUuid() {
        if (getSession().isAdminSession())
            return accountUuid;
        return getSession().getAccountUuid();
    }

    public String getEndpointUuid() {
        return EndpointUuid;
    }

    public void setEndpointUuid(String EndpointUuid) {
        this.EndpointUuid = EndpointUuid;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }
}
