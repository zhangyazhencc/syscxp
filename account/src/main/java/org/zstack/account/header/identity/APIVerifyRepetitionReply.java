package org.zstack.account.header.identity;

import org.zstack.header.message.APIReply;

/**
 * Created by wangwg on 2017/09/11.
 */
public class APIVerifyRepetitionReply extends APIReply {

    private boolean accountName;
    private boolean AccountEmail;
    private boolean AccountPhone;
    private boolean userName;

    public boolean isAccountName() {
        return accountName;
    }

    public void setAccountName(boolean accountName) {
        this.accountName = accountName;
    }

    public boolean isAccountEmail() {
        return AccountEmail;
    }

    public void setAccountEmail(boolean accountEmail) {
        AccountEmail = accountEmail;
    }

    public boolean isAccountPhone() {
        return AccountPhone;
    }

    public void setAccountPhone(boolean accountPhone) {
        AccountPhone = accountPhone;
    }

    public boolean isUserName() {
        return userName;
    }

    public void setUserName(boolean userName) {
        this.userName = userName;
    }
}
