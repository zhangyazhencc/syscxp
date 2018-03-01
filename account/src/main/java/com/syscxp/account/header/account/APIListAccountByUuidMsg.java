package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.List;

@SuppressUserCredentialCheck
@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIListAccountByUuidMsg extends APISyncCallMessage {

    @APIParam(nonempty = true)
    private List<String> accountUuidList;

    public List<String> getAccountUuidList() {
        return accountUuidList;
    }

    public void setAccountUuidList(List<String> accountUuidList) {
        this.accountUuidList = accountUuidList;
    }

}
