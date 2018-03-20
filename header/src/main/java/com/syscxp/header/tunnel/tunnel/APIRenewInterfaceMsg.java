package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.vpn.vpn.VpnVO;
import org.springframework.http.HttpMethod;

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIRenewInterfaceReply.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APIRenewInterfaceMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, resourceType = InterfaceVO.class, checkAccount = true)
    private String uuid;

    @APIParam
    private Integer duration;

    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_WEEK", "BY_DAY"})
    private ProductChargeModel productChargeModel;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
                ntfy("Renew InterfaceVO")
                        .resource(uuid, InterfaceVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
