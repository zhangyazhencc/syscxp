package org.zstack.account.header.identity.updatemsg;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/8.
 */
public class APIChangeUserPhoneMsg extends APIMessage {
    @APIParam
    private String uuid;

    @APIParam(maxLength = 2048)
    private String phone;

    @APIParam(maxLength = 2048)
    private String code;

    @APIParam(maxLength = 2048)
    private boolean isupdate;

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

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
