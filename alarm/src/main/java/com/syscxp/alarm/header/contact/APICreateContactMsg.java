package com.syscxp.alarm.header.contact;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.core.validation.Validation;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

@Action(services = {AlarmConstant.ACTION_SERVICE}, category = AlarmConstant.ACTION_CATEGORY_ALARM, names = {"create"})
public class APICreateContactMsg extends APIMessage{

    @APIParam(emptyString = false,required = false)
    private String accountUuid;

    @APIParam(emptyString = false)
    private String name;

    @APIParam(emptyString = false,required = false)
    private String email;

    @APIParam(emptyString = false)
    private String mobile;

    @APIParam(required = false)
    private String mobileCaptcha;

    @APIParam(required = false)
    private String emailCaptcha;

    @APIParam(required = false)
    private List<String> ways;

    public List<String> getWays() {
        return ways;
    }

    public void setWays(List<String> ways) {
        this.ways = ways;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAccountUuid() {
        if (getSession().getType() == AccountType.SystemAdmin) {
            return accountUuid;
        } else {
            return getSession().getAccountUuid();
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getMobileCaptcha() {
        return mobileCaptcha;
    }

    public void setMobileCaptcha(String mobileCaptcha) {
        this.mobileCaptcha = mobileCaptcha;
    }

    public String getEmailCaptcha() {
        return emailCaptcha;
    }

    public void setEmailCaptcha(String emailCaptcha) {
        this.emailCaptcha = emailCaptcha;
    }
}
