package com.syscxp.header.idc.solution;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.idc.IdcConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.idc.SolutionConstant;

import java.math.BigDecimal;

@Action(services = {IdcConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "create")
public class APICreateSolutionInterfaceMsg extends  APIMessage {

    @APIParam(maxLength = 32, resourceType = SolutionVO.class, checkAccount = true)
    private String solutionUuid;
    @APIParam(maxLength = 255)
    private String name;
    @APIParam(maxLength = 32)
    private String endpointUuid;
    @APIParam(maxLength = 32)
    private String portOfferingUuid;
    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK", "BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam
    private int duration;
    @APIParam
    private BigDecimal cost;
    @APIParam
    private BigDecimal discount;
    @APIParam
    private BigDecimal shareDiscount;

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
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

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getPortOfferingUuid() {
        return portOfferingUuid;
    }

    public void setPortOfferingUuid(String portOfferingUuid) {
        this.portOfferingUuid = portOfferingUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getShareDiscount() {
        return shareDiscount;
    }

    public void setShareDiscount(BigDecimal shareDiscount) {
        this.shareDiscount = shareDiscount;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateSolutionTunnelEvent) evt).getTunnelInventory().getUuid();
                }
                ntfy("Create SolutionTunnelVO")
                        .resource(uuid, SolutionTunnelVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
