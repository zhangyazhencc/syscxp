package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.switchs.SwitchPortType;

/**
 * Created by DCY on 2017-09-08
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"create"})
public class APICreateInterfaceMsg extends APIMessage {

    @APIParam(required = false, maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false, maxLength = 128)
    private String name;
    @APIParam(emptyString = false, maxLength = 32, resourceType = EndpointVO.class)
    private String endpointUuid;
    @APIParam
    private SwitchPortType portType;
    @APIParam(required = false, maxLength = 255)
    private String description;
    @APIParam
    private Integer duration;
    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_DAY"})
    private ProductChargeModel productChargeModel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAccountUuid() {
        if (getSession().getType() == AccountType.SystemAdmin) {
            return accountUuid;
        } else {
            return getSession().getAccountUuid();
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public SwitchPortType getPortType() {
        return portType;
    }

    public void setPortType(SwitchPortType portType) {
        this.portType = portType;
    }
}