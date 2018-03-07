package com.syscxp.account.header.identity;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_USER)
public class APIMailOrPhoneIsExistentMsg extends APISyncCallMessage  {

    @APIParam(maxLength = 128, required = false)
    private String accountMail;
    @APIParam(maxLength = 128, required = false)
    private String accountPhone;

    @APIParam(maxLength = 128, required = false)
    private String userMail;
    @APIParam(maxLength = 128, required = false)
    private String userPhone;

    public String getAccountMail() {
        return accountMail;
    }

    public void setAccountMail(String accountMail) {
        this.accountMail = accountMail;
    }

    public String getAccountPhone() {
        return accountPhone;
    }

    public void setAccountPhone(String accountPhone) {
        this.accountPhone = accountPhone;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
