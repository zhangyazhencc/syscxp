package com.syscxp.vpn.vpn;

import com.syscxp.core.validation.ConditionalValidation;
import com.syscxp.header.vpn.agent.CertInfo;

import java.util.List;
import java.util.Map;

/**
 * @author wangjie
 */
public class VpnCommands {
    public static class AgentCommand {
        public String vpnuuid;
    }

    public static class AgentResponse implements ConditionalValidation {
        private boolean success = true;
        private String error;
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

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

    public static class ConnectResponse extends AgentResponse {
        public String vpnSucc;
    }

    public static class VpnStatusResponse extends AgentResponse {
        public String vpnStatus;
    }

    /**
     * 上传证书：/vpn/push_cert
     */
    public static class PushCertCmd extends AgentCommand {
        public CertInfo certinfo;
    }

    public static class PushCertRsp extends AgentResponse {
    }

    /**
     * vpn配置：/vpn/conf_vpn
     */
    public static class VpnConfCmd extends AgentCommand {
        public String hostip;
        public String vpnport;

    }

    public static class VpnConfRsp extends ConnectResponse {
    }

    /**
     * VPN服务：/vpn/vpn_service
     */
    public static class VpnServiceCmd extends AgentCommand {
        public  String vpnvlanid;
        public String vpnport;
        public String command;
    }


    public static class VpnServiceRsp extends VpnStatusResponse {
    }

    /**
     * Vpn接口：/vpn/vport
     */
    public static class VpnPortCmd extends AgentCommand {
        public  String vpnvlanid;
        public  String ddnport;
        public String vpnport;
        public String command;
    }

    public static class VpnPortRsp extends VpnStatusResponse {
    }

    /**
     * 限速：/vpn/rate_limiting
     */
    public static class RateLimitingCmd extends AgentCommand {
        public  String vpnport;
        public  String speed;
        public String command;
    }

    public static class RateLimitingRsp extends AgentResponse {
        String vpnLimit;
    }

    /**
     * VPN重连:/vpn/start-all
     */
    public static class StartAllCmd extends AgentCommand {
        public  String vpnvlanid;
        public  String ddnport;
        public  String vpnport;
        public String speed;

    }

    public static class StartAllRsp extends VpnStatusResponse {
    }

    /**
     * 删除VPN：/vpn/destroy-vpn
     */
    public static class DestroyVpnCmd extends AgentCommand {
        public String vpnvlanid;
        public String ddnport;
        public  String vpnport;
    }

    public static class DestroyVpnRsp extends VpnStatusResponse {
    }

    /**
     * VPN信息：/vpn/client_info
     */
    public static class ClientInfoCmd extends AgentCommand {
    }

    public static class ClientInfoRsp extends AgentResponse {
        public String client_conf;
    }

    /**
     * VPN信息：/vpn/login_info
     */
    public static class LoginInfoCmd extends AgentCommand {
        public String username;
        public String passwd;
    }

    public static class LoginInfoRsp extends AgentResponse {
        String passwdfile;
    }

    /**
     * VPN初始化：/vpn/init_vpn
     */
    public static class InitVpnCmd extends AgentCommand {
        public String hostip;
        public String vpnvlanid;
        public String vpnport;
        public String ddnport;
        public String speed;
        public CertInfo certinfo;
    }

    public static class InitVpnRsp extends VpnStatusResponse {
    }

    /**
     * VPN状态：/vpn/vpn_status
     */
    public static class VpnStatusCmd extends AgentCommand {
        public List<String> vpnuuids;
    }

    public static class VpnStatusRsp extends VpnStatusResponse {
        public Map<String, String> states;
    }

}
