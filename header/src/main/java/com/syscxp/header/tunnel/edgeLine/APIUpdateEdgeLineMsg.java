package com.syscxp.header.tunnel.edgeLine;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.EdgeLineConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/1/11
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = EdgeLineConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateEdgeLineMsg extends APIMessage {

    @APIParam(emptyString = false, resourceType = EdgeLineVO.class)
    private String uuid;

    @APIParam(required = false,numberRange = {0,Integer.MAX_VALUE})
    private Integer prices;

    @APIParam(required = false,emptyString = false, maxLength = 32)
    private String type;

    @APIParam(required = false,emptyString = false, maxLength = 255)
    private String destinationInfo;

    @APIParam(required = false,emptyString = false, maxLength = 255)
    private String description;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getPrices() {
        return prices;
    }

    public void setPrices(Integer prices) {
        this.prices = prices;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestinationInfo() {
        return destinationInfo;
    }

    public void setDestinationInfo(String destinationInfo) {
        this.destinationInfo = destinationInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update EdgeLineVO")
                        .resource(uuid, EdgeLineVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
