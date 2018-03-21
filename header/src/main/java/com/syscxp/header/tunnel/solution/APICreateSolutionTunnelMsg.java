package com.syscxp.header.tunnel.solution;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointVO;
import com.syscxp.header.tunnel.tunnel.PortOfferingVO;
import org.springframework.http.HttpMethod;

import java.math.BigDecimal;


@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "create")
public class APICreateSolutionTunnelMsg extends  APIMessage {

    @APIParam(maxLength = 32, resourceType = SolutionVO.class)
    private String solutionUuid;
    @APIParam(numberRange = {0,Long.MAX_VALUE})
    private BigDecimal cost;
    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK", "BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam(maxLength = 32)
    private int duration;

    @APIParam(maxLength = 32, resourceType = EndpointVO.class)
    private String endpointUuidA;
    @APIParam(maxLength = 32, resourceType = EndpointVO.class)
    private String endpointUuidZ;

    @APIParam(emptyString = false,maxLength = 32,resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;

    @APIParam(emptyString = false,required = false,resourceType = InnerConnectedEndpointVO.class)
    private String innerConnectedEndpointUuid;

    @APIParam(maxLength = 32, required = false, resourceType = PortOfferingVO.class)
    private String portOfferingUuidA;
    @APIParam(maxLength = 32, required = false, resourceType = PortOfferingVO.class)
    private String portOfferingUuidZ;

    @APIParam(maxLength = 255)
    private String name;

    @APIParam
    private boolean isShareA;
    @APIParam
    private boolean isShareZ;

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

    public String getEndpointUuidA() {
        return endpointUuidA;
    }

    public void setEndpointUuidA(String endpointUuidA) {
        this.endpointUuidA = endpointUuidA;
    }

    public String getEndpointUuidZ() {
        return endpointUuidZ;
    }

    public void setEndpointUuidZ(String endpointUuidZ) {
        this.endpointUuidZ = endpointUuidZ;
    }

    public String getPortOfferingUuidA() {
        return portOfferingUuidA;
    }

    public void setPortOfferingUuidA(String portOfferingUuidA) {
        this.portOfferingUuidA = portOfferingUuidA;
    }

    public String getPortOfferingUuidZ() {
        return portOfferingUuidZ;
    }

    public void setPortOfferingUuidZ(String portOfferingUuidZ) {
        this.portOfferingUuidZ = portOfferingUuidZ;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getInnerConnectedEndpointUuid() {
        return innerConnectedEndpointUuid;
    }

    public void setInnerConnectedEndpointUuid(String innerConnectedEndpointUuid) {
        this.innerConnectedEndpointUuid = innerConnectedEndpointUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShareA() {
        return isShareA;
    }

    public void setShareA(boolean shareA) {
        isShareA = shareA;
    }

    public boolean isShareZ() {
        return isShareZ;
    }

    public void setShareZ(boolean shareZ) {
        isShareZ = shareZ;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateSolutionInterfaceEvent) evt).getInterfaceInventory().getUuid();
                }
                ntfy("Create SolutionInterfaceVO")
                        .resource(uuid, SolutionInterfaceVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
