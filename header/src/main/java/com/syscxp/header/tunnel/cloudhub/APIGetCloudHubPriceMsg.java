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
public class APIGetCloudHubPriceMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,maxLength = 32,resourceType = CloudHubOfferingVO.class)
    private String cloudHubOfferingUuid;
    @APIParam
    private Integer duration;
    @APIParam(emptyString = false,validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK","BY_DAY"})
    private ProductChargeModel productChargeModel;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getCloudHubOfferingUuid() {
        return cloudHubOfferingUuid;
    }

    public void setCloudHubOfferingUuid(String cloudHubOfferingUuid) {
        this.cloudHubOfferingUuid = cloudHubOfferingUuid;
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
