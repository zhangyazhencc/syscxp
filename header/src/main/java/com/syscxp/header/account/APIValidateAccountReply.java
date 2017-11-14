package com.syscxp.header.account;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIValidateAccountReply extends APIReply {

    private boolean validAccount;
    private boolean normalAccountHasProxy;

    private List<String> accountUuidOwnProxy;

    public List<String> getAccountUuidOwnProxy() {
        return accountUuidOwnProxy;
    }

    public void setAccountUuidOwnProxy(List<String> accountUuidOwnProxy) {
        this.accountUuidOwnProxy = accountUuidOwnProxy;
    }

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
