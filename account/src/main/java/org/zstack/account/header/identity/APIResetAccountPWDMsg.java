package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = AccountConstant.ACTION_CATEGORY, names = {"acount_pwd"}, accountOnly = true)
public class APIResetAccountPWDMsg extends  APIMessage implements  AccountMessage {

    @APIParam(maxLength = 32)
    private String targetUuid;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }
}
