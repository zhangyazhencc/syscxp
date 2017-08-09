package org.zstack.account.header.identity;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/9.
 */
public class APIChangeUserEmailMsg extends APIMessage {
    @APIParam
    private String accountUuid;

    @APIParam
    private String userName;

    @APIParam
    private String newEmail;

    public String getAccountUuid() {
        return accountUuid;
    }

    public String getUserName() {
        return userName;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
