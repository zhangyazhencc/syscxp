package com.syscxp.sms.header;

import com.syscxp.header.message.APIReply;


public class APIValidateMailCodeReply extends APIReply {

    private boolean valid;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
