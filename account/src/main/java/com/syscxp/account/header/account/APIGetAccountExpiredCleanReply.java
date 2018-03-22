package com.syscxp.account.header.account;


import com.syscxp.header.message.APIReply;

public class APIGetAccountExpiredCleanReply extends APIReply{

    private boolean expiredClean;

    public boolean isExpiredClean() {
        return expiredClean;
    }

    public void setExpiredClean(boolean expiredClean) {
        this.expiredClean = expiredClean;
    }

}
