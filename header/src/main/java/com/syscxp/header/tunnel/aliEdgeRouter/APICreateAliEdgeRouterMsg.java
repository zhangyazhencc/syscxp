package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = AliEdgeRouterConstant.ACTION_CATEGORY, names = {"create"})
public class APICreateAliEdgeRouterMsg extends APIMessage{
    @APIParam(required = false,maxLength = 32)
    private String accountUuid;
    @APIParam(maxLength = 32, resourceType = TunnelVO.class)
    private String tunnelUuid;
    @APIParam(maxLength = 64)
    private String aliAccountUuid;
    @APIParam(maxLength = 64)
    private String aliRegionId;
    @APIParam(maxLength = 32)
    private String physicalLineUuid;
    @APIParam
    private Integer vlan;

    public String getAccountUuid() {
        if(getSession().getType() == AccountType.SystemAdmin){
            return accountUuid;
        }else{
            return getSession().getAccountUuid();
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getAliAccountUuid() {
        return aliAccountUuid;
    }

    public void setAliAccountUuid(String aliAccountUuid) {
        this.aliAccountUuid = aliAccountUuid;
    }

    public String getAliRegionId() {
        return aliRegionId;
    }

    public void setAliRegionId(String aliRegionId) {
        this.aliRegionId = aliRegionId;
    }

    public String getPhysicalLineUuid() {
        return physicalLineUuid;
    }

    public void setPhysicalLineUuid(String physicalLineUuid) {
        this.physicalLineUuid = physicalLineUuid;
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
                    uuid = ((APICreateAliEdgeRouterEvent) evt).getInventory().getUuid();
                }

                ntfy("Create AliEdgeRouterVO")
                        .resource(uuid, AliEdgeRouterVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
