package com.syscxp.account.header.account;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.List;

@SuppressCredentialCheck
public class APIListAccountByUuidMsg extends APISyncCallMessage {

    @APIParam(nonempty = false)
    private List<String> accountUuidList;

    public List<String> getAccountUuidList() {
        return accountUuidList;
    }

    public void setAccountUuidList(List<String> accountUuidList) {
        this.accountUuidList = accountUuidList;
    }

}
