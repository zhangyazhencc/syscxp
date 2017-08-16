package org.zstack.sms.header;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by zxhread on 17/8/15.
 */
public class APIGetVerificationCodeMsg extends APIMessage{

    @APIParam(nonempty = true, validRegexValues = "^1[3,4,5,7,8]\\d{9}$")
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
