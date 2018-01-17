package com.syscxp.alarm.header.log;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.NeedReplyMessage;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-01-12.
 * @Description: .
 */

public class APIHandleTunnelAlarmMsg extends NeedReplyMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;

    @APIParam(emptyString = false,maxLength = 32)
    private String tunnelUuid;

    @APIParam(emptyString = false,maxLength = 32)
    private String regulationUuid;

    @APIParam(maxLength = 256)
    private String problem;

    @APIParam(maxLength = 256)
    private String smsContent;

    @APIParam(maxLength = 256)
    private String mailContent;

    @APIParam(maxLength = 127,validValues = {"OK","PROBLEM"})
    private AlarmStatus status;

    @APIParam(emptyString = false,maxLength = 128)
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

    public String getRegulationUuid() {
        return regulationUuid;
    }

    public void setRegulationUuid(String regulationUuid) {
        this.regulationUuid = regulationUuid;
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

    public AlarmStatus getStatus() {
        return status;
    }

    public void setStatus(AlarmStatus status) {
        this.status = status;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
