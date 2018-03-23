package com.syscxp.header.tunnel.network;

import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2018/3/12
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = L3NetWorkConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateL3EndpointBandwidthMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = L3EndPointVO.class)
    private String uuid;
    @APIParam(emptyString = false,maxLength = 32,resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBandwidthOfferingUuid() {
        return bandwidthOfferingUuid;
    }

    public void setBandwidthOfferingUuid(String bandwidthOfferingUuid) {
        this.bandwidthOfferingUuid = bandwidthOfferingUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update L3EndPoint Bandwidth")
                        .resource(uuid, L3EndPointVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
