package org.zstack.account.header.identity;

import org.zstack.header.message.APISyncCallMessage;
import org.zstack.header.identity.Action;

@Action(category = AccountConstant.ACTION_CATEGORY_USER, names = {"read"})
public class APIGetUserMsg extends APISyncCallMessage implements  AccountMessage {
    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

}
