package com.syscxp.account.header.identity;

import com.syscxp.header.identity.APISessionMessage;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;

@SuppressCredentialCheck
public class APILogInByAccountMsg extends APISessionMessage {
    @APIParam(required = false)
    private String phone;

    @APIParam(required = false)
    private String email;

    @APIParam(required = false)
    private String accountName;

    @APIParam
    private String password;
    
    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
