package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;

@Action(category = AccountConstant.ACTION_CATEGORY, names = {"account", "read"})
public class APIGetAccountMsg extends  APIMessage implements  AccountMessage {

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }


}
