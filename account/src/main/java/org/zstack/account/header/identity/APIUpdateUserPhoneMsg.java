package org.zstack.account.header.identity;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/8.
 */
public class APIUpdateUserPhoneMsg extends APIMessage implements AccountMessage{

    @APIParam(maxLength = 32)
    private String newPhone;

    @APIParam(maxLength = 36)
    private String code;

    public String getNewPhone() {
        return newPhone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNewPhone(String newPhone) {
        this.newPhone = newPhone;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
