package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;

import java.util.List;

/**
 * Create by DCY on 2018/1/31
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateQinqMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = TunnelVO.class, checkAccount = true)
    private String uuid;

    @APIParam(emptyString = false, resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceUuidA;

    @APIParam(emptyString = false, resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceUuidZ;

    @APIParam
    private boolean isQinqA;

    @APIParam
    private boolean isQinqZ;

    @APIParam(required = false)
    private List<InnerVlanSegment> vlanSegment;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getInterfaceUuidA() {
        return interfaceUuidA;
    }

    public void setInterfaceUuidA(String interfaceUuidA) {
        this.interfaceUuidA = interfaceUuidA;
    }

    public String getInterfaceUuidZ() {
        return interfaceUuidZ;
    }

    public void setInterfaceUuidZ(String interfaceUuidZ) {
        this.interfaceUuidZ = interfaceUuidZ;
    }

    public boolean isQinqA() {
        return isQinqA;
    }

    public void setQinqA(boolean qinqA) {
        isQinqA = qinqA;
    }

    public boolean isQinqZ() {
        return isQinqZ;
    }

    public void setQinqZ(boolean qinqZ) {
        isQinqZ = qinqZ;
    }

    public List<InnerVlanSegment> getVlanSegment() {
        return vlanSegment;
    }

    public void setVlanSegment(List<InnerVlanSegment> vlanSegment) {
        this.vlanSegment = vlanSegment;
    }
}
