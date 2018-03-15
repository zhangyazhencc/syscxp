package com.syscxp.header.tunnel.edgeLine;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.notification.ApiNotification;

/**
 * Create by DCY on 2018/1/12
 */
@InnerCredentialCheck
public class APIRenewAutoEdgeLineMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, resourceType = EdgeLineVO.class)
    private String uuid;

    @APIParam
    private Integer duration;

    @APIParam(validValues = {"BY_MONTH", "BY_YEAR", "BY_DAY"})
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
                ntfy("Renew auto EdgeLineVO")
                        .resource(uuid, EdgeLineVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
