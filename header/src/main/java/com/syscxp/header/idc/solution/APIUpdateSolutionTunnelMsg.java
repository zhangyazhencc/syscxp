package com.syscxp.header.idc.solution;

import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.idc.IdcConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.idc.SolutionConstant;

import java.math.BigDecimal;

@Action(services = {IdcConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "update")
public class APIUpdateSolutionTunnelMsg extends  APIMessage {

    @APIParam(maxLength = 32, resourceType = SolutionTunnelVO.class)
    private String uuid;

    @APIParam(emptyString = false,required = false, resourceType = BandwidthOfferingVO.class)
    private String bandwidthOfferingUuid;

    @APIParam
    private BigDecimal cost;

    @APIParam
    private BigDecimal discount;

    @APIParam
    private BigDecimal shareDiscount;

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getShareDiscount() {
        return shareDiscount;
    }

    public void setShareDiscount(BigDecimal shareDiscount) {
        this.shareDiscount = shareDiscount;
    }

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
                ntfy("Update SolutionTunnelVO")
                        .resource(uuid, SolutionTunnelVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
