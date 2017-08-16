package org.zstack.account.header.identity;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/8.
 */
public class APIUpdateAccountPhoneMsg extends APIMessage implements AccountMessage{
    @APIParam
    private String newphone;

    @APIParam
    private String code;

    public String getNewphone() {
        return newphone;
    }

    public String getCode() {
        return code;
    }

    public void setNewphone(String newphone) {
        this.newphone = newphone;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

}
