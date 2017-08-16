package org.zstack.account.header.identity;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/8.
 */
public class APIUpdateAccountPhoneMsg extends APIMessage {
    @APIParam
    private String uuid;

    @APIParam
    private String phone;

    @APIParam
    private String code;

    @APIParam
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
