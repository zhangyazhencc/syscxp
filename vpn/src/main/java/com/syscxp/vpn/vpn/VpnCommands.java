package com.syscxp.vpn.vpn;

import com.syscxp.core.validation.ConditionalValidation;

public class VpnCommands {
    public static class AgentCommand {
    }

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

    public static class ConnectResponse extends AgentResponse {
        public String vpnSucc;
    }

    public static class VpnStatusResponse extends AgentResponse {
        public String vpnSatus;
    }

    /**
     * 创建化证书：/vpn/create_cert
     */
    public static class CreateCertCmd extends AgentCommand {
        public String vpnuuid;
    }

    public static class CreateCertRsp extends AgentResponse {
        public  String vpnCert;
    }

    /**
     * vpn配置：/vpn/conf_vpn
     */
    public static class VpnConfCmd extends AgentCommand {
        public String vpnuuid;
        public String hostip;
        public String vpnport;

    }

    public static class VpnConfRsp extends ConnectResponse {
    }

    /**
     * VPN服务：/vpn/vpn_service
     */
    public static class VpnServiceCmd extends AgentCommand {
        public  String vpnuuid;
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
        public String vpnuuid;
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
        public String vpnuuid;
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
        public String vpnuuid;
    }

    public static class ClientInfoRsp extends AgentResponse {
        public String ca_crt;
        public String client_crt;
        public String client_key;
        public String client_conf;
    }

    /**
     * VPN信息：/vpn/login_info
     */
    public static class LoginInfoCmd extends AgentCommand {
        public String vpnuuid;
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
        public String vpnuuid;
        public String hostip;
        public String vpnvlanid;
        public String vpnport;
        public String ddnport;
        public String speed;
        public String username;
        public String passwd;
    }

    public static class InitVpnRsp extends AgentResponse {
    }

}
