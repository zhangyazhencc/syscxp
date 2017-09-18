package org.zstack.header.account;

import org.zstack.header.message.APIReply;

public class APIValidateAccountReply extends APIReply {

    private boolean validAccount;

    public boolean isValidAccount() {
        return validAccount;
    }

    public void setValidAccount(boolean validAccount) {
        this.validAccount = validAccount;
    }
}
