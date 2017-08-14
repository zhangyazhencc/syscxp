package org.zstack.account.header;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/8.
 */
public class APIChangeAccountPWDMsg extends APIMessage {
    @APIParam
    private String uuid;

    @APIParam
    private String phoneOrEmail;

    @APIParam
    private String code;

    @APIParam
    private boolean isupdate;

    @APIParam
    private String oldpassword;

    @APIParam(maxLength = 2048)
    private String newpassword;

    public String getUuid() {
        return uuid;
    }

    public String getPhoneOrEmail() {
        return phoneOrEmail;
    }

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

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setPhoneOrEmail(String phoneOrEmail) {
        this.phoneOrEmail = phoneOrEmail;
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

}
