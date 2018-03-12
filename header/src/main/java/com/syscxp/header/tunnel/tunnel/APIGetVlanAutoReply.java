package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/11/3
 */
@RestResponse(fieldsTo = {"all"})
public class APIGetVlanAutoReply extends APIReply {

    private Integer vlanA;

    private Integer vlanZ;

    public Integer getVlanA() {
        return vlanA;
    }

    public void setVlanA(Integer vlanA) {
        this.vlanA = vlanA;
    }

    public Integer getVlanZ() {
        return vlanZ;
    }

    public void setVlanZ(Integer vlanZ) {
        this.vlanZ = vlanZ;
    }
}
