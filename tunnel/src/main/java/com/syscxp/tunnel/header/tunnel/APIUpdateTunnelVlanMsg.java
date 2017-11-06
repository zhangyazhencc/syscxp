package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/11/6
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateTunnelVlanMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true)
    private String uuid;

    @APIParam(emptyString = false,required = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceAUuid;
    @APIParam(numberRange = {1, 4094},required = false)
    private Integer aVlan;
    @APIParam(emptyString = false,required = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceZUuid;
    @APIParam(numberRange = {1, 4094},required = false)
    private Integer zVlan;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getInterfaceAUuid() {
        return interfaceAUuid;
    }

    public void setInterfaceAUuid(String interfaceAUuid) {
        this.interfaceAUuid = interfaceAUuid;
    }

    public Integer getaVlan() {
        return aVlan;
    }

    public void setaVlan(Integer aVlan) {
        this.aVlan = aVlan;
    }

    public String getInterfaceZUuid() {
        return interfaceZUuid;
    }

    public void setInterfaceZUuid(String interfaceZUuid) {
        this.interfaceZUuid = interfaceZUuid;
    }

    public Integer getzVlan() {
        return zVlan;
    }

    public void setzVlan(Integer zVlan) {
        this.zVlan = zVlan;
    }
}
