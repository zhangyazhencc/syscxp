package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIListProvinceNodeReply extends APIReply {
    List<String> province;

    public List<String> getProvince() {
        return province;
    }

    public void setProvince(List<String> province) {
        this.province = province;
    }
}
