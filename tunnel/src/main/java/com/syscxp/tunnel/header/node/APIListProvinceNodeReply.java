package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIListProvinceNodeReply extends APIReply {
    List<String> privinces;

    public List<String> getPrivinces() {
        return privinces;
    }

    public void setPrivinces(List<String> privinces) {
        this.privinces = privinces;
    }
}
