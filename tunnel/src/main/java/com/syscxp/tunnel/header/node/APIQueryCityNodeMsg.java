package com.syscxp.tunnel.header.node;

import com.syscxp.header.query.APIQueryMessage;

public class APIQueryCityNodeMsg extends APIQueryMessage{
    private String provice;

    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }
}
