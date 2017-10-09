package com.syscxp.header.account;

import com.syscxp.header.message.APIReply;

public class APIValidateAccountReply extends APIReply {

    private boolean validAccount;

    public boolean isValidAccount() {
        return validAccount;
    }

    public void setValidAccount(boolean validAccount) {
        this.validAccount = validAccount;
    }
}
