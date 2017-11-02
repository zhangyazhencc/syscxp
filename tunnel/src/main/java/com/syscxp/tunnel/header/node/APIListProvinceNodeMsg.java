package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

public class APIListProvinceNodeMsg extends APISyncCallMessage {
    @APIParam(required = false)
    private String country;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
