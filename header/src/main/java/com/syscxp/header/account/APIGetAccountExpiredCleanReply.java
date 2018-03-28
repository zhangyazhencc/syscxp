package com.syscxp.header.account;


import com.syscxp.header.message.APIReply;

public class APIGetAccountExpiredCleanReply extends APIReply{

    private String uuid;

    private boolean expiredClean;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isExpiredClean() {
        return expiredClean;
    }

    public void setExpiredClean(boolean expiredClean) {
        this.expiredClean = expiredClean;
    }

}
