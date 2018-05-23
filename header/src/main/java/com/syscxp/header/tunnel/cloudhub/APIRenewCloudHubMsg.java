package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.CloudHubConstant;
import com.syscxp.header.tunnel.TunnelConstant;

import java.math.BigDecimal;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = CloudHubConstant.ACTION_CATEGORY, names = {"renew"})
public class APIRenewCloudHubMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String uuid;

    @APIParam(emptyString = false)
    private ProductChargeModel productChargeModel;

    @APIParam(emptyString = false)
    private int duration;

    @APIParam(emptyString = false)
    private BigDecimal cost;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public ApiNotification _notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Renewal Idc")
                        .resource(uuid, CloudHubVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
