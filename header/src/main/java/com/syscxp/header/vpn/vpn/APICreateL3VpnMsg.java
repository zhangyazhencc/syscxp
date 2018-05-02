package com.syscxp.header.vpn.vpn;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {VpnConstant.ACTION_SERVICE}, category = VpnConstant.ACTION_CATEGORY_VPN, names = {"create"})
public class APICreateL3VpnMsg extends APIVpnMessage {
    @APIParam(emptyString = false, minLength = 6, maxLength = 50)
    private String name;
    @APIParam(required = false, maxLength = 255)
    private String description;
    @APIParam(resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;
    @APIParam(emptyString = false)
    private String resourceUuid;
    @APIParam(emptyString = false)
    private String resourceType;
    @APIParam(emptyString = false)
    private String l3EndpointUuid;
    @APIParam(emptyString = false)
    private String endpointUuid;
    @APIParam(emptyString = false)
    private String workMode;
    @APIParam(emptyString = false)
    private String startIp;
    @APIParam(emptyString = false)
    private String endIp;
    @APIParam(emptyString = false)
    private String netmask;
    @APIParam(emptyString = false)
    private String gateway;
    @APIParam(emptyString = false)
    private String remoteIp;
    @APIParam(emptyString = false)
    private String monitorIp;
    @APIParam(numberRange = {1, Integer.MAX_VALUE})
    private Integer duration;
    @APIParam(required = false, validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK", "BY_DAY"})
    private ProductChargeModel productChargeModel;
    @APIParam(numberRange = {1, Integer.MAX_VALUE})
    private Integer vlan;
    @APIParam(resourceType = VpnCertVO.class, checkAccount = true)
    private String vpnCertUuid;

    public String getResourceUuid() { return resourceUuid; }

    public void setResourceUuid(String resourceUuid) { this.resourceUuid = resourceUuid; }

    public String getResourceType() { return resourceType; }

    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getL3EndpointUuid() { return l3EndpointUuid; }

    public void setL3EndpointUuid(String l3EndpointUuid) { this.l3EndpointUuid = l3EndpointUuid; }

    public String getEndpointUuid() { return endpointUuid; }

    public void setEndpointUuid(String endpointUuid) { this.endpointUuid = endpointUuid; }

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

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
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
                    uuid = ((APICreateL3VpnEvent) evt).getInventory().getUuid();
                }

                ntfy("Create L3VpnVO")
                        .resource(uuid, L3VpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
