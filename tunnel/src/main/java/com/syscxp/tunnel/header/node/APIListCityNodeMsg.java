package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APISyncCallMessage;

public class APIListCityNodeMsg extends APISyncCallMessage {

    private String provice;

    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }
}
