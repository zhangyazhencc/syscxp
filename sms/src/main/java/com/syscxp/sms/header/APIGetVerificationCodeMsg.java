package com.syscxp.sms.header;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

/**
 * Created by zxhread on 17/8/15.
 */
@SuppressCredentialCheck
public class APIGetVerificationCodeMsg extends APISyncCallMessage {

    @APIParam(nonempty = true, required = true, validRegexValues = "^1[3,4,5,7,8]\\d{9}$")
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
