package org.zstack.account.header.identity;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/8.
 */
public class APIUpdateAccountPWDMsg extends APIMessage implements AccountMessage {

    @APIParam
    private String code;

    @APIParam
    private boolean isupdate;

    @APIParam
    private String oldpassword;

    @APIParam(maxLength = 2048)
    private String newpassword;


    public String getCode() {
        return code;
    }

    public boolean isIsupdate() {
        return isupdate;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setIsupdate(boolean isupdate) {
        this.isupdate = isupdate;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }


    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
