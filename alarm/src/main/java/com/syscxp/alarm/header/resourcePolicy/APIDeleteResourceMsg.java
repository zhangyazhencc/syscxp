package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

public class APIDeleteResourceMsg extends APIMessage{
    @APIParam
    private String tunnel_id;

    public String getTunnel_id() {
        return tunnel_id;
    }

    public void setTunnel_id(String tunnel_id) {
        this.tunnel_id = tunnel_id;
    }
}
