package org.zstack.account.header;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/9.
 */
public class APIUpdateAccountEmailMsg extends APIMessage {
    @APIParam
    private String uuid;

    @APIParam
    private String email;

    @APIParam
    private String code;

    @APIParam
    private boolean isupdate;

    public String getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setIsupdate(boolean isupdate) {
        this.isupdate = isupdate;
    }
}
