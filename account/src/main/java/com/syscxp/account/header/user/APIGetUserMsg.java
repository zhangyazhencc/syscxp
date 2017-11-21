package com.syscxp.account.header.user;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.account.AccountMessage;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.identity.Action;

@SuppressUserCredentialCheck
@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_USER, names = {"read"})
public class APIGetUserMsg extends APISyncCallMessage implements AccountMessage {
    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

}
