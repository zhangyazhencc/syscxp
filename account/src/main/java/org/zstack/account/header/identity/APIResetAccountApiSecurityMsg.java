package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;


@Action(category = AccountConstant.ACTION_CATEGORY, names = {"api_key"}, accountOnly = true)
public class APIResetAccountApiSecurityMsg extends APIMessage implements AccountMessage{

    @APIParam(emptyString = false, required = true, validRegexValues = "^1[3,4,5,7,8]\\d{9}$")
    String phone;

    @APIParam(emptyString = false, maxLength = 6)
    String code;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
