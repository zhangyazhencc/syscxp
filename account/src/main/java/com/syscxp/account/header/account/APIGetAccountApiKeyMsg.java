package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"read"})
public class APIGetAccountApiKeyMsg extends APISyncCallMessage implements  AccountMessage {

    @APIParam(maxLength = 32)
    private String phone;

    @APIParam(maxLength = 32)
    private String code;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getPhone() {
        return phone;
    }

    public String getCode() {
        return code;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
