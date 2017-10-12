package com.syscxp.account.header.identity;

import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APIReply;

/**
 * Created by wangwg on 2017/09/11.
 */
public class APIVerifyRepetitionReply extends APIReply {

    private boolean accountName;
    private boolean accountEmail;
    private boolean accountPhone;
    private boolean userName;
    private boolean userEmail;
    private boolean userPhone;
    private boolean roleName;

    public boolean isAccountName() {
        return accountName;
    }

    public void setAccountName(boolean accountName) {
        this.accountName = accountName;
    }

    public boolean isAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(boolean accountEmail) {
        this.accountEmail = accountEmail;
    }

    public boolean isUserEmail() {
        return userEmail;
    }

    public void setUserEmail(boolean userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isUserPhone() {
        return userPhone;
    }

    public void setUserPhone(boolean userPhone) {
        this.userPhone = userPhone;
    }

    public boolean isRoleName() {
        return roleName;
    }

    public void setRoleName(boolean roleName) {
        this.roleName = roleName;
    }

    public boolean isAccountPhone() {
        return accountPhone;
    }

    public void setAccountPhone(boolean accountPhone) {
        this.accountPhone = accountPhone;
    }

    public boolean isUserName() {
        return userName;
    }

    public void setUserName(boolean userName) {
        this.userName = userName;
    }

}
