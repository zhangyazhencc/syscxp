package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.CloudHubConstant;
import com.syscxp.header.tunnel.TunnelConstant;


@Action(services = {TunnelConstant.ACTION_SERVICE}, category = CloudHubConstant.ACTION_CATEGORY, names = {"create"})
public class APICreateCloudHubMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String name;

    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;

    @APIParam(emptyString = false,maxLength = 32)
    private String interfaceUuid;

    @APIParam(emptyString = false,maxLength = 32)
    private String endpointUuid;

    @APIParam(emptyString = false,maxLength = 32,resourceType = CloudHubOfferingVO.class)
    private String cloudHubOfferingUuid;

    @APIParam(emptyString = false,required = false)
    private String description;

    @APIParam
    private Integer duration;

    @APIParam(emptyString = false,validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK", "BY_DAY"})
    private ProductChargeModel productChargeModel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getCloudHubOfferingUuid() {
        return cloudHubOfferingUuid;
    }

    public void setCloudHubOfferingUuid(String cloudHubOfferingUuid) {
        this.cloudHubOfferingUuid = cloudHubOfferingUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateCloudHubEvent) evt).getInventory().getUuid();
                }

                ntfy("Create CloudHub")
                        .resource(uuid, CloudHubVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
