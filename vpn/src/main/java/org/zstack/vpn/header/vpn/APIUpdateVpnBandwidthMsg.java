package org.zstack.vpn.header.vpn;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
import org.zstack.vpn.vpn.VpnConstant;

import java.util.List;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"update"}, adminOnly = true)
public class APIUpdateVpnBandwidthMsg extends APIMessage{
    @APIParam(resourceType = VpnVO.class, checkAccount = true)
    private String uuid;
    @APIParam
    private Long bandwidth;
    @APIParam
    private List<String> productPriceUnitUuids;

    public List<String> getProductPriceUnitUuids() {
        return productPriceUnitUuids;
    }

    public void setProductPriceUnitUuids(List<String> productPriceUnitUuids) {
        this.productPriceUnitUuids = productPriceUnitUuids;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update VpnVO bandwidth")
                        .resource(uuid, VpnVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
