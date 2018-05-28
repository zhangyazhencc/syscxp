package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.CloudHubConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/5/28
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = CloudHubConstant.ACTION_CATEGORY, names = {"read"})
public class APIGetRenewCloudHubPriceMsg extends APISyncCallMessage {
    @APIParam(emptyString = false, resourceType = CloudHubVO.class, checkAccount = true)
    private String uuid;

    @APIParam
    private Integer duration;

    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK", "BY_DAY"})
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
}
