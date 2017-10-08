package com.syscxp.vpn.header.vpn;

import com.syscxp.header.billing.ProductPriceUnit;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.vpn.vpn.VpnConstant;

import java.util.List;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"create"}, adminOnly = true)
public class APICreateVpnMsg extends APIVpnMassage {
    @APIParam(emptyString = false)
    private String name;
    @APIParam(required = false)
    private String description;
    @APIParam(emptyString = false)
    private String hostUuid;
    @APIParam(emptyString = false)
    private String vpnCidr;
    @APIParam
    private Long bandwidth;
    @APIParam(emptyString = false)
    private String endpointUuid;
    @APIParam(emptyString = false)
    private Integer duration;
    @APIParam(emptyString = false)
    private String networkUuid;
    @APIParam(emptyString = false)
    private String localIp;
    @APIParam(emptyString = false)
    private String netmask;
    @APIParam
    private String vlan;
    @APIParam
    private List<ProductPriceUnit> productPriceUnits;

    public List<ProductPriceUnit> getProductPriceUnits() {
        return productPriceUnits;
    }

    public void setProductPriceUnits(List<ProductPriceUnit> productPriceUnits) {
        this.productPriceUnits = productPriceUnits;
    }

    public String getNetworkUuid() {
        return networkUuid;
    }

    public void setNetworkUuid(String networkUuid) {
        this.networkUuid = networkUuid;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getVlan() {
        return vlan;
    }

    public void setVlan(String vlan) {
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

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getVpnCidr() {
        return vpnCidr;
    }

    public void setVpnCidr(String vpnCidr) {
        this.vpnCidr = vpnCidr;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
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
                        .resource(uuid, VpnVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
