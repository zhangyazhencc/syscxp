package com.syscxp.header.tunnel.solution;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;

import java.math.BigDecimal;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "create")
public class APICreateSolutionVpnMsg extends  APIMessage {

    @APIParam(maxLength = 32, resourceType = SolutionVO.class)
    private String solutionUuid;
    @APIParam(numberRange = {0,Long.MAX_VALUE})
    private BigDecimal cost;
    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam(maxLength = 32)
    private int duration;

    @APIParam(maxLength = 128, resourceType = SolutionTunnelVO.class)
    private String solutionTunnelUuid;
    @APIParam(maxLength = 128, resourceType = EndpointVO.class)
    private String endpointUuid;
    @APIParam(maxLength = 128, resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;
    @APIParam(maxLength = 255)
    private String name;

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
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

    public String getSolutionTunnelUuid() {
        return solutionTunnelUuid;
    }

    public void setSolutionTunnelUuid(String solutionTunnelUuid) {
        this.solutionTunnelUuid = solutionTunnelUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateSolutionVpnEvent) evt).getVpnInventory().getUuid();
                }
                ntfy("Create SolutionVpnVO")
                        .resource(uuid, SolutionVpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
