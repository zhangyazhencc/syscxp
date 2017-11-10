package com.syscxp.header.tunnel.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.NodeConstant;

@Action(category = NodeConstant.ACTION_CATEGORY, names = {"read"})
public class APIListCityNodeMsg extends APISyncCallMessage {

    private String provice;

    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }
}
