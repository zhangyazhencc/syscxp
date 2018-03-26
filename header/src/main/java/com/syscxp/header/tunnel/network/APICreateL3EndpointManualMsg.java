package com.syscxp.header.tunnel.network;

import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.tunnel.InterfaceVO;

/**
 * Create by DCY on 2018/3/20
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = L3NetWorkConstant.ACTION_CATEGORY, names = {"create"}, adminOnly = true)
public class APICreateL3EndpointManualMsg extends APIMessage {

    @APIParam(emptyString = false, resourceType = L3NetworkVO.class, checkAccount = true)
    private String l3NetworkUuid;
    @APIParam(emptyString = false, resourceType = EndpointVO.class)
    private String endpointUuid;
    @APIParam(emptyString = false, maxLength = 32, resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;
    @APIParam(emptyString = false, resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceUuid;
    @APIParam(numberRange = {1, 4094})
    private Integer vlan;

    public String getL3NetworkUuid() {
        return l3NetworkUuid;
    }

    public void setL3NetworkUuid(String l3NetworkUuid) {
        this.l3NetworkUuid = l3NetworkUuid;
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

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateL3EndpointManualEvent) evt).getInventory().getUuid();
                }
                ntfy("Create L3EndpointVO")
                        .resource(uuid, L3EndpointVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
