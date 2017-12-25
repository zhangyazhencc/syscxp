package com.syscxp.vpn.host;

import com.syscxp.core.validation.ConditionalValidation;

import java.util.List;

/**
 * @author wangjie
 */
public class VpnHostCommands {
    public static class AgentResponse implements ConditionalValidation {
        private boolean success = true;
        private String error;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
            this.success = false;
        }

        @Override
        public boolean needValidation() {
            return success;
        }
    }

    public static class AgentCommand {
    }

    public static class PingCmd extends AgentCommand {
        public String hostUuid;
    }

    public static class PingResponse extends AgentResponse {
        private String hostUuid;

        public String getHostUuid() {
            return hostUuid;
        }

        public void setHostUuid(String hostUuid) {
            this.hostUuid = hostUuid;
        }
    }

    public static class ConnectCmd extends AgentCommand {
        private String hostUuid;
        private String sendCommandUrl;
        private List<String> iptablesRules;

        public List<String> getIptablesRules() {
            return iptablesRules;
        }

        public void setIptablesRules(List<String> iptablesRules) {
            this.iptablesRules = iptablesRules;
        }

        public String getSendCommandUrl() {
            return sendCommandUrl;
        }

        public void setSendCommandUrl(String sendCommandUrl) {
            this.sendCommandUrl = sendCommandUrl;
        }

        public String getHostUuid() {
            return hostUuid;
        }

        public void setHostUuid(String hostUuid) {
            this.hostUuid = hostUuid;
        }
    }

    public static class ConnectResponse extends AgentResponse {
    }

    public static class ReconnectMeCmd {
        public String hostUuid;
        public String reason;
    }
}
