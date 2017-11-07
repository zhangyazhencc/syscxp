package com.syscxp.alarm.header.log;

public class AlarmLogCallbackCmd {
    private String accountUuid;
    private String tunnelUuid;
    private String regulationUuid;
    private String problem;
    private String smsContent;
    private String mailContent;
    private String status;
    private String eventId;

    public static AlarmLogCallbackCmd valueOf(AlarmLogVO alarmLogVO){
        AlarmLogCallbackCmd cmd = new AlarmLogCallbackCmd();
        cmd.setAccountUuid(alarmLogVO.getAccountUuid());
        cmd.setTunnelUuid(alarmLogVO.getProductUuid());
        cmd.setRegulationUuid(alarmLogVO.getRegulationUuid());
        cmd.setProblem(alarmLogVO.getAlarmContent());
        cmd.setSmsContent(alarmLogVO.getAlarmContent());
        cmd.setMailContent(alarmLogVO.getMailContent());
        cmd.setStatus(alarmLogVO.getStatus());
        cmd.setEventId(alarmLogVO.getEventId());

        return cmd;
    }

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
