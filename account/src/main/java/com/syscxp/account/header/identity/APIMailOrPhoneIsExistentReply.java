package com.syscxp.account.header.identity;

import com.syscxp.header.message.APIReply;

public class APIMailOrPhoneIsExistentReply extends APIReply {

    private boolean accountMail;
    private boolean accountPhone;
    private boolean userMail;
    private boolean userPhone;

    public boolean isAccountMail() {
        return accountMail;
    }

    public void setAccountMail(boolean accountMail) {
        this.accountMail = accountMail;
    }

    public boolean isAccountPhone() {
        return accountPhone;
    }

    public void setAccountPhone(boolean accountPhone) {
        this.accountPhone = accountPhone;
    }

    public boolean isUserMail() {
        return userMail;
    }

    public void setUserMail(boolean userMail) {
        this.userMail = userMail;
    }

    public boolean isUserPhone() {
        return userPhone;
    }

    public void setUserPhone(boolean userPhone) {
        this.userPhone = userPhone;
    }

}
