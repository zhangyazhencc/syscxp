package com.syscxp.sms.header;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.util.List;

@InnerCredentialCheck
public class APISendAlarmSmsMsg extends APISyncCallMessage {

    @APIParam
    private String phone;

    @APIParam
    private String data;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
