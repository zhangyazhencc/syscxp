package com.syscxp.sms.header;

import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import java.util.List;

@InnerCredentialCheck
public class APISendAlarmSmsMsg extends APIMessage {

    @APIParam(nonempty = true)
    private String phone;

    @APIParam(nonempty = true)
    private String templateId;

    @APIParam(nonempty = true)
    private List<String> data;

    private String ip;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public void setIp(String ip) {
        this.ip = ip;
    }
}
