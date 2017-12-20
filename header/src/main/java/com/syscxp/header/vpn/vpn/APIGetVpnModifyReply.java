package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;

public class APIGetVpnModifyReply extends APIReply {
    private Integer maxModifies;

    private Integer modifies;

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public Integer getModifies() {
        return modifies;
    }

    public void setModifies(Integer modifies) {
        this.modifies = modifies;
    }
}
