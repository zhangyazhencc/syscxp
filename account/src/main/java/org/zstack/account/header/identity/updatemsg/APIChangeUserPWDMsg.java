package org.zstack.account.header.identity.updatemsg;

import org.zstack.header.identity.APISessionMessage;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/8.
 */
public class APIChangeUserPWDMsg extends APISessionMessage {
    @APIParam
    private String uuid;

    @APIParam
    private String phone;

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

    public String getPhone() {
        return phone;
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
    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setIsupdate(boolean isupdate) {
        this.isupdate = isupdate;
    }
}
