package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;


public class APIUpdateL3RouteMsg extends APIMessage {

    @APIParam
    private String uuid;
    @APIParam(required = false)
    private String cidr;
    @APIParam(required = false)
    private String nextIp;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public String getNextIp() {
        return nextIp;
    }

    public void setNextIp(String nextIp) {
        this.nextIp = nextIp;
    }
}
