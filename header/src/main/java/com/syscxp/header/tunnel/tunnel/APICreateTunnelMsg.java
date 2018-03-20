package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

/**
 * Created by DCY on 2017-09-11
 */

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APICreateTunnelEvent.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"create"})
public class APICreateTunnelMsg extends APIMessage {

    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false,maxLength = 32,resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String endpointAUuid;
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String endpointZUuid;
    @APIParam(emptyString = false,required = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceAUuid;
    @APIParam(emptyString = false,required = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceZUuid;
    @APIParam
    private Integer duration;
    @APIParam(emptyString = false,validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK","BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam(emptyString = false,required = false)
    private String description;
    @APIParam(emptyString = false,required = false,maxLength = 32,resourceType = EndpointVO.class)
    private String innerConnectedEndpointUuid;
    @APIParam(emptyString = false,required = false,maxLength = 32,resourceType = InterfaceVO.class)
    private String crossInterfaceUuid;
    @APIParam(emptyString = false,required = false,maxLength = 32,resourceType = TunnelVO.class)
    private String crossTunnelUuid;

    public String getAccountUuid() {
        if(getSession().getType() == AccountType.SystemAdmin){
            return accountUuid;
        }else{
            return getSession().getAccountUuid();
        }
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

    public String getInterfaceAUuid() {
        return interfaceAUuid;
    }

    public void setInterfaceAUuid(String interfaceAUuid) {
        this.interfaceAUuid = interfaceAUuid;
    }

    public String getInterfaceZUuid() {
        return interfaceZUuid;
    }

    public void setInterfaceZUuid(String interfaceZUuid) {
        this.interfaceZUuid = interfaceZUuid;
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

    public String getEndpointAUuid() {
        return endpointAUuid;
    }

    public void setEndpointAUuid(String endpointAUuid) {
        this.endpointAUuid = endpointAUuid;
    }

    public String getEndpointZUuid() {
        return endpointZUuid;
    }

    public void setEndpointZUuid(String endpointZUuid) {
        this.endpointZUuid = endpointZUuid;
    }

    public String getCrossInterfaceUuid() {
        return crossInterfaceUuid;
    }

    public void setCrossInterfaceUuid(String crossInterfaceUuid) {
        this.crossInterfaceUuid = crossInterfaceUuid;
    }

    public String getCrossTunnelUuid() {
        return crossTunnelUuid;
    }

    public void setCrossTunnelUuid(String crossTunnelUuid) {
        this.crossTunnelUuid = crossTunnelUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateTunnelEvent) evt).getInventory().getUuid();
                }

                ntfy("Create TunnelVO")
                        .resource(uuid, TunnelVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
