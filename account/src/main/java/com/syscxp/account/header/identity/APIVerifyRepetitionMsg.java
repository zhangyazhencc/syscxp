package com.syscxp.account.header.identity;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

/**
 * Created by wangwg on 2017/09/11.
 */

@SuppressCredentialCheck
public class APIVerifyRepetitionMsg extends APISyncCallMessage {

    @APIParam(maxLength = 128, required = false)
    private String accountName;
    @APIParam(maxLength = 128, required = false)
    private String accountEmail;
    @APIParam(maxLength = 32, required = false)
    private String accountPhone;

    @APIParam(maxLength = 128, required = false)
    private String userName;
    @APIParam(maxLength = 128, required = false)
    private String userEmail;
    @APIParam(maxLength = 128, required = false)
    private String userPhone;

    @APIParam(maxLength = 128, required = false)
    private String roleName;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getAccountPhone() {
        return accountPhone;
    }

    public void setAccountPhone(String accountPhone) {
        this.accountPhone = accountPhone;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
