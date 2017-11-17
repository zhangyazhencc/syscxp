package com.syscxp.sms.header;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.rest.APINoSee;

import java.util.List;

/**
 * Created by zxhread on 17/8/14.
 */
public class APISendSmsMsg extends APIMessage {

    @APIParam(nonempty = true)
    private List<String> phone;

    @APIParam(nonempty = true)
    private String appId;

    @APIParam(nonempty = true)
    private String templateId;

    @APIParam(nonempty = true)
    private List<String> data;

    @APINoSee
    private String ipConfine;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
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

    public String getIpConfine() {
        return ipConfine;
    }

    public void setIpConfine(String ipConfine) {
        this.ipConfine = ipConfine;
    }
}
