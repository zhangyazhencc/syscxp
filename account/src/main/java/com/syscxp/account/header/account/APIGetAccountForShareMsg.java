package com.syscxp.account.header.account;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@SuppressCredentialCheck
public class APIGetAccountForShareMsg extends APISyncCallMessage {

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

}
