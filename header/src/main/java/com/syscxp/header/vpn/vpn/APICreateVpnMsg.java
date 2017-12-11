package com.syscxp.header.vpn.vpn;

import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.vpn.VpnConstant;


@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"create"})
public class APICreateVpnMsg extends APIVpnMessage {
    @APIParam(emptyString = false, minLength = 6, maxLength = 50)
    private String name;
    @APIParam(required = false, maxLength = 255)
    private String description;
    @APIParam(resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;
    @APIParam(emptyString = false)
    private String endpointUuid;
    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private Integer duration;
    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private Integer vlan;
    @APIParam(resourceType = VpnCertVO.class, checkAccount = true)
    private String vpnCertUuid;

    public String getVpnCertUuid() {
        return vpnCertUuid;
    }

    public void setVpnCertUuid(String vpnCertUuid) {
        this.vpnCertUuid = vpnCertUuid;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateVpnEvent) evt).getInventory().getUuid();
                }

                ntfy("Create VpnVO")
                        .resource(uuid, VpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
