package com.syscxp.sms.header;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;


@SuppressCredentialCheck
public class APIValidateMailCodeMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String mail;

    @APIParam(emptyString = false, maxLength = 8)
    private String code;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
