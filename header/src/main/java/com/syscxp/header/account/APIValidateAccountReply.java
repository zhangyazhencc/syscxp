package com.syscxp.header.account;

import com.syscxp.header.message.APIReply;

public class APIValidateAccountReply extends APIReply {

    private boolean validAccount;
    private boolean normalAccountHasProxy;

    public boolean isValidAccount() {
        return validAccount;
    }

    public void setValidAccount(boolean validAccount) {
        this.validAccount = validAccount;
    }

    public boolean isNormalAccountHasProxy() {
        return normalAccountHasProxy;
    }

    public void setNormalAccountHasProxy(boolean normalAccountHasProxy) {
        this.normalAccountHasProxy = normalAccountHasProxy;
    }
}
