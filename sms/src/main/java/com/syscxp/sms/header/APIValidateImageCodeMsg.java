package com.syscxp.sms.header;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;


@SuppressCredentialCheck
public class APIValidateImageCodeMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String uuid;

    @APIParam(emptyString = false, maxLength = 12)
    private String code;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
