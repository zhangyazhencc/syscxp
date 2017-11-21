package com.syscxp.header.account;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIValidateAccountReply extends APIReply {

    private boolean validAccount;

    private AccountType type;

    public boolean isValidAccount() {
        return validAccount;
    }

    public void setValidAccount(boolean validAccount) {
        this.validAccount = validAccount;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }
}
