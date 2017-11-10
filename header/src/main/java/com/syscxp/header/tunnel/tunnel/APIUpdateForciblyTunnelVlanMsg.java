package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/11/6
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateForciblyTunnelVlanMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true)
    private String uuid;

    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceAUuid;
    @APIParam(numberRange = {1, 4094})
    private Integer aVlan;
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceZUuid;
    @APIParam(numberRange = {1, 4094})
    private Integer zVlan;

    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String oldInterfaceAUuid;
    @APIParam(numberRange = {1, 4094})
    private Integer oldAVlan;
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String oldInterfaceZUuid;
    @APIParam(numberRange = {1, 4094})
    private Integer oldZVlan;

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

    public String getOldInterfaceAUuid() {
        return oldInterfaceAUuid;
    }

    public void setOldInterfaceAUuid(String oldInterfaceAUuid) {
        this.oldInterfaceAUuid = oldInterfaceAUuid;
    }

    public Integer getOldAVlan() {
        return oldAVlan;
    }

    public void setOldAVlan(Integer oldAVlan) {
        this.oldAVlan = oldAVlan;
    }

    public String getOldInterfaceZUuid() {
        return oldInterfaceZUuid;
    }

    public void setOldInterfaceZUuid(String oldInterfaceZUuid) {
        this.oldInterfaceZUuid = oldInterfaceZUuid;
    }

    public Integer getOldZVlan() {
        return oldZVlan;
    }

    public void setOldZVlan(Integer oldZVlan) {
        this.oldZVlan = oldZVlan;
    }
}
