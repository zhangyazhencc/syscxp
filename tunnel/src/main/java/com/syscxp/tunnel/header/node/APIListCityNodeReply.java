package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIListCityNodeReply extends APIReply {
    List<String> citys;

    public List<String> getCitys() {
        return citys;
    }

    public void setCitys(List<String> citys) {
        this.citys = citys;
    }
}
