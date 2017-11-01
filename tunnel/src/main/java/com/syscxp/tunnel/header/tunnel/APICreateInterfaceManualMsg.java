package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.billing.ProductPriceUnit;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.tunnel.header.endpoint.EndpointVO;
import com.syscxp.tunnel.header.switchs.SwitchPortVO;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateInterfaceManualMsg extends APIMessage {


    @APIParam(emptyString = false, maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false, maxLength = 128)
    private String name;
    @APIParam(emptyString = false, maxLength = 32, resourceType = SwitchPortVO.class)
    private String switchPortUuid;
    @APIParam(emptyString = false, maxLength = 32, resourceType = EndpointVO.class)
    private String endpointUuid;
    @APIParam(emptyString = false, maxLength = 32, resourceType = PortOfferingVO.class)
    private String portOfferingUuid;
    @APIParam(required = false, validValues = {"TRUNK", "ACCESS", "QINQ"})
    private NetworkType type;
    @APIParam(emptyString = false, maxLength = 255)
    private String description;
    @APIParam
    private Integer duration;
    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_DAY"})
    private ProductChargeModel productChargeModel;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
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

    public NetworkType getType() {
        return type;
    }

    public void setType(NetworkType type) {
        this.type = type;
    }

    public String getPortOfferingUuid() {
        return portOfferingUuid;
    }

    public void setPortOfferingUuid(String portOfferingUuid) {
        this.portOfferingUuid = portOfferingUuid;
    }
}
