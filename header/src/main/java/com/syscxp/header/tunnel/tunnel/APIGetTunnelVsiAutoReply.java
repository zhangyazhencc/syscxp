package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2018/3/20
 */
@RestResponse(fieldsTo = {"all"})
public class APIGetTunnelVsiAutoReply extends APIReply {

    private Integer vsi;

    public Integer getVsi() {
        return vsi;
    }

    public void setVsi(Integer vsi) {
        this.vsi = vsi;
    }
}
