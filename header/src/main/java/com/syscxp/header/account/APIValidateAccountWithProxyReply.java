package com.syscxp.header.account;

import com.syscxp.header.message.APIReply;

public class APIValidateAccountWithProxyReply extends APIReply {

    private boolean hasRelativeAccountWithProxy;

    public boolean isHasRelativeAccountWithProxy() {
        return hasRelativeAccountWithProxy;
    }

    public void setHasRelativeAccountWithProxy(boolean hasRelativeAccountWithProxy) {
        this.hasRelativeAccountWithProxy = hasRelativeAccountWithProxy;
    }
}
