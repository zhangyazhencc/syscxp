package org.zstack.account.header.account;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.SuppressUserCredentialCheck;
import org.zstack.header.message.APISyncCallMessage;

@SuppressUserCredentialCheck
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIGetAccountMsg extends APISyncCallMessage implements  AccountMessage {

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }


}
