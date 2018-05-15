package com.syscxp.header.vpn.monitor;

import com.syscxp.header.message.APIReply;

public class APIQueryOpentsdbConditionReply extends APIReply {

    private String vpnUuid;

    private String endpoint;

    private String iface;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getIface() {
        return iface;
    }

    public void setIface(String iface) {
        this.iface = iface;
    }

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }
}
