package org.zstack.account.header.identity;

import org.zstack.header.identity.SuppressCredentialCheck;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/09/11.
 */

@SuppressCredentialCheck
public class APIVerifyRepetitionMsg extends APIMessage {

    @APIParam(maxLength = 128, required = false)
    private String accountName;

    @APIParam(maxLength = 128, required = false)
    private String AccountEmail;

    @APIParam(maxLength = 32, required = false)
    private String AccountPhone;

    @APIParam(maxLength = 128, required = false)
    private String userName;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountEmail() {
        return AccountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        AccountEmail = accountEmail;
    }

    public String getAccountPhone() {
        return AccountPhone;
    }

    public void setAccountPhone(String accountPhone) {
        AccountPhone = accountPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
