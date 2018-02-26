package com.syscxp.account.header.account;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

public class APIGetAccountForShareMsg extends APISyncCallMessage implements  AccountMessage {

    @APIParam(emptyString = false,required = false)
    private String accountName;

    @APIParam(emptyString = false,required = false)
    private String accountPhone;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountPhone() {
        return accountPhone;
    }

    public void setAccountPhone(String accountPhone) {
        this.accountPhone = accountPhone;
    }

    @Override
    public String getAccountUuid() {
        return getSession().getAccountUuid();
    }
}
