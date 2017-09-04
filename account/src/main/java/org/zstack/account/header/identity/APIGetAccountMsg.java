package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APISyncCallMessage;

@Action(category = AccountConstant.ACTION_CATEGORY, accountOnly = true)
public class APIGetAccountMsg extends APISyncCallMessage implements  AccountMessage {

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }


}
