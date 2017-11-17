package com.syscxp.header.sms;

import com.syscxp.header.message.APIReply;

/**
 * Created by zxhread on 17/8/15.
 */
public class APIValidateVerificationCodeReply extends APIReply {

    private boolean valid;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
