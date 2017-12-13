package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.message.APIReply;

/**
 * Create by DCY on 2017/12/11
 */
public class APIGetModifyBandwidthNumReply extends APIReply {

    private Integer maxModifies;

    private Integer hasModifies;

    private Integer leftModifies;

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public Integer getHasModifies() {
        return hasModifies;
    }

    public void setHasModifies(Integer hasModifies) {
        this.hasModifies = hasModifies;
    }

    public Integer getLeftModifies() {
        return leftModifies;
    }

    public void setLeftModifies(Integer leftModifies) {
        this.leftModifies = leftModifies;
    }
}