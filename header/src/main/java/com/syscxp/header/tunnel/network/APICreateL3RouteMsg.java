package com.syscxp.header.tunnel.network;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;


public class APICreateL3RouteMsg extends APIMessage {

    @APIParam
    private String l3EndPointUuid;
    @APIParam
    private String cidr;
    @APIParam
    private String nextIp;

    public String getL3EndPointUuid() {
        return l3EndPointUuid;
    }

    public void setL3EndPointUuid(String l3EndPointUuid) {
        this.l3EndPointUuid = l3EndPointUuid;
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
