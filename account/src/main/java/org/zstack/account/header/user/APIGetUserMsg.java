package org.zstack.account.header.user;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.account.header.account.AccountMessage;
import org.zstack.header.message.APISyncCallMessage;
import org.zstack.header.identity.Action;

@Action(category = AccountConstant.ACTION_CATEGORY_USER, names = {"read"})
public class APIGetUserMsg extends APISyncCallMessage implements AccountMessage {
    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

}
