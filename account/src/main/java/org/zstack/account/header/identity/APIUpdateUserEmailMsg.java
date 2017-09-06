package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/9.
 */
@Action(category = AccountConstant.ACTION_CATEGORY, names = {"user"})
public class APIUpdateUserEmailMsg extends APIMessage implements AccountMessage{

    @APIParam
    private String newEmail;

    @APIParam
    private String code;

    public String getNewEmail() {
        return newEmail;
    }

    public String getCode() {
        return code;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
