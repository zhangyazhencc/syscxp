package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIListCountryNodeReply extends APIReply {
    List<String> countrys;

    public List<String> getCountrys() {
        return countrys;
    }

    public void setCountrys(List<String> countrys) {
        this.countrys = countrys;
    }
}
