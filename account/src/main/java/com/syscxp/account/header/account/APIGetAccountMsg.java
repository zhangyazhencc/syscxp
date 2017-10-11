package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.message.APISyncCallMessage;

@SuppressUserCredentialCheck
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIGetAccountMsg extends APISyncCallMessage implements  AccountMessage {

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }


}