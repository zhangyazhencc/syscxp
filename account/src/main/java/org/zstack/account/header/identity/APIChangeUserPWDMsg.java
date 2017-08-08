package org.zstack.account.header.identity;

import org.zstack.header.identity.APISessionMessage;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/8.
 */
@Action(category = AccountConstant.ACTION_CATEGORY)
public class APIChangeUserPWDMsg extends APISessionMessage {
    @APIParam
    private String accountUuid;

    @APIParam
    private String username;

    @APIParam
    private String oldpassword;

    @APIParam(maxLength = 2048)
    private String newpassword;

    public String getAccountUuid() {
        return accountUuid;
    }

    public String getUsername() {
        return username;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
}
