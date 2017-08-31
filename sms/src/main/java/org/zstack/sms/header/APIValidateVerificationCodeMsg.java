package org.zstack.sms.header;

import org.zstack.header.identity.SuppressCredentialCheck;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

/**
 * Created by zxhread on 17/8/16.
 */
@SuppressCredentialCheck
public class APIValidateVerificationCodeMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, required = true, validRegexValues = "^1[3,4,5,7,8]\\d{9}$")
    private String phone;

    @APIParam(emptyString = false, maxLength = 6)
    private String code;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
