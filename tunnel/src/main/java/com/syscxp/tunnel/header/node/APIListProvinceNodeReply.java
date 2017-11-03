package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIListProvinceNodeReply extends APIReply {
    List<String> provinces;

    public List<String> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<String> provinces) {
        this.provinces = provinces;
    }
}
