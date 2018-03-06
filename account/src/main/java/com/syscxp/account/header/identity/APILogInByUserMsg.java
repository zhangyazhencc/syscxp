package com.syscxp.account.header.identity;

import com.syscxp.header.identity.APISessionMessage;
import com.syscxp.header.identity.PasswordNoSee;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;

@SuppressCredentialCheck
public class APILogInByUserMsg extends APISessionMessage {
    @APIParam(required = false)
    private String accountName;
    @APIParam(required = false)
    @PasswordNoSee
    private String accountPhone;
    @APIParam(required = false)
    @PasswordNoSee
    private String accountEmail;

    @APIParam(required = false)
    private String userName;
    @APIParam(required = false)
    @PasswordNoSee
    private String userPhone;
    @APIParam(required = false)
    @PasswordNoSee
    private String userEmail;

    @APIParam
    @PasswordNoSee
    private String password;

    @APIParam
    private String imageUuid;

    @APIParam
    private String imageCode;

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

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }
}
