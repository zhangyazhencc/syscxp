package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = AccountConstant.ACTION_CATEGORY, accountOnly = true)
public class APIGetAccountApiKeyMsg extends  APIMessage implements  AccountMessage {

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
