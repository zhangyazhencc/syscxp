package com.syscxp.alarm.header.log;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.sql.Timestamp;

@Action(category = AlarmConstant.ACTION_CATEGORY_ALARM_LOG)
public class APICreateAlarmLogMsg extends APIMessage{

    @APIParam(emptyString = false)
    private String  accountUuid;

    @APIParam(emptyString = false)
    private String tunnelUuid;

    @APIParam(emptyString = false)
    private String tunnelName;

    @APIParam(emptyString = false)
    private String ruleUuid;

    @APIParam(emptyString = false)
    private String problem;

    @APIParam(emptyString = false)
    private String smsContent;

    @APIParam(emptyString = false)
    private String mailContent;

    @APIParam(emptyString = false)
    private String status;

    @APIParam(emptyString = false)
    private String eventId;


    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public String getRuleUuid() {
        return ruleUuid;
    }

    public void setRuleUuid(String ruleUuid) {
        this.ruleUuid = ruleUuid;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
