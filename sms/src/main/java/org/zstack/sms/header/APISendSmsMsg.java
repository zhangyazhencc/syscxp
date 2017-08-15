package org.zstack.sms.header;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.util.List;

/**
 * Created by zxhread on 17/8/14.
 */
public class APISendSmsMsg extends APIMessage {

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
