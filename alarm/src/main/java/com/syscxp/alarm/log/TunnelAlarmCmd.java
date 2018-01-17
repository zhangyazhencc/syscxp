package com.syscxp.alarm.log;

import com.syscxp.alarm.header.log.AlarmStatus;

import java.util.List;

public class TunnelAlarmCmd {

    public static class TunnelAlarmLog{
        private String accountUuid;
        private String tunnelUuid;
        private String regulationUuid;
        private String problem;
        private String smsContent;
        private String mailContent;
        private AlarmStatus status;
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

    public static class TunnelInfo{
        private String tunnelUuid;
        private String tunnelName;
        private String accountUuid;
        private String endpointAMip;
        private String endpointZMip;
        private long bandwidth;
        private String nodeA;
        private String nodeZ;

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

        public String getAccountUuid() {
            return accountUuid;
        }

        public void setAccountUuid(String accountUuid) {
            this.accountUuid = accountUuid;
        }

        public String getEndpointAMip() {
            return endpointAMip;
        }

        public void setEndpointAMip(String endpointAMip) {
            this.endpointAMip = endpointAMip;
        }

        public String getEndpointZMip() {
            return endpointZMip;
        }

        public void setEndpointZMip(String endpointZMip) {
            this.endpointZMip = endpointZMip;
        }

        public long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public String getNodeA() {
            return nodeA;
        }

        public void setNodeA(String nodeA) {
            this.nodeA = nodeA;
        }

        public String getNodeZ() {
            return nodeZ;
        }

        public void setNodeZ(String nodeZ) {
            this.nodeZ = nodeZ;
        }
    }

    public static class TunnelAlarmResponse{
        private boolean success;
        private String msg;
        private List<TunnelInfo> tunnelInfos;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<TunnelInfo> getTunnelInfos() {
            return tunnelInfos;
        }

        public void setTunnelInfos(List<TunnelInfo> tunnelInfos) {
            this.tunnelInfos = tunnelInfos;
        }
    }
}
