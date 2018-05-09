package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

public class APIDeleteExpiredRenewReply extends APIReply {

    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
