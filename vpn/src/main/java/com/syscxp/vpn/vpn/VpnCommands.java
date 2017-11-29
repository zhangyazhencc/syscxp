package com.syscxp.vpn.vpn;

import com.syscxp.header.vpn.vpn.VpnVO;


public class VpnCommands {
    public static class VpnAgentCommand {
        String vpnuuid;
    }

    public static class VpnAgentResponse {
        TaskResult result;
        RunStatus status;

        public TaskResult getResult() {
            return result;
        }

        public void setResult(TaskResult result) {
            this.result = result;
        }

        public RunStatus getStatus() {
            return status;
        }

        public void setStatus(RunStatus status) {
            this.status = status;
        }
    }

    public static class TaskResult {
        String message;
        boolean success = false;

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
    }

    /**
     * 物理机或VPN运行状态
     */
    public enum RunStatus {
        DOWN,
        UP,
        UNKOWN
    }

    /**
     * c初始化证书：/vpn/init_cert
     */
    public static class InitCertCmd extends VpnAgentCommand {
        public static InitCertCmd valueOf(VpnVO vo) {
            InitCertCmd cmd = new InitCertCmd();
            cmd.vpnuuid = vo.getUuid();
            return cmd;
        }
    }

    public static class InitCertResponse extends VpnAgentResponse {
    }

    /**
     * VPN服务：/vpn/vpn_service
     */
    public static class VpnServiceCmd extends VpnAgentCommand {
        String vpnvlanid;
        String vpnport;
        // start,stop,status
        String command;

        public static VpnServiceCmd valueOf(VpnVO vo) {
            VpnServiceCmd cmd = new VpnServiceCmd();
            cmd.vpnuuid = vo.getUuid();
            cmd.vpnvlanid = vo.getVlan().toString();
            cmd.vpnport = vo.getPort().toString();
            return cmd;
        }
    }

    public static class VpnServiceResponse extends VpnAgentResponse {
    }

    /**
     * vpn配置：/vpn/conf_vpn
     */
    public static class VpnConfCmd extends VpnAgentCommand {
        String hostip;
        String vpnport;

        public static VpnConfCmd valueOf(VpnVO vo) {
            VpnConfCmd cmd = new VpnConfCmd();
            cmd.vpnuuid = vo.getUuid();
            cmd.hostip = vo.getVpnHost().getHostIp();
            cmd.vpnport = vo.getPort().toString();
            return cmd;
        }
    }

    public static class VpnConfResponse extends VpnAgentResponse {
    }

    /**
     * Vpn接口：/vpn/vport
     */
    public static class VportCmd extends VpnAgentCommand {
        String vpnvlanid;
        String ddnport;
        String vpnport;
        // add,del
        String command;
        public static VportCmd valueOf(VpnVO vo) {
            VportCmd cmd = new VportCmd();
            cmd.ddnport = vo.getTunnelInterface();
            cmd.vpnvlanid = vo.getVlan().toString();
            cmd.vpnport = vo.getPort().toString();
            return cmd;
        }
    }

    public static class VportResponse extends VpnAgentResponse {
    }

    /**
     * 限速：/vpn/rate_limiting
     */
    public static class RateLimitingCmd extends VpnAgentCommand {
        String vpnport;
        String speed;
        String command;
        public static RateLimitingCmd valueOf(VpnVO vo) {
            RateLimitingCmd cmd = new RateLimitingCmd();
            cmd.vpnport = vo.getPort().toString();
            cmd.speed = vo.getBandwidthOfferingUuid();
            cmd.command = "clean";
            return cmd;
        }
    }
    public static class RateLimitingResponse extends VpnAgentResponse {
    }

    /**
     * VPN重连:/vpn/start-all
     */
    public static class StartAllCmd extends VpnAgentCommand {
        String vpnvlanid;
        String ddnport;
        String vpnport;
        String speed;

        public static StartAllCmd valueOf(VpnVO vo) {
            StartAllCmd cmd = new StartAllCmd();
            cmd.vpnuuid = vo.getUuid();
            cmd.vpnvlanid = vo.getVlan().toString();
            cmd.vpnport = vo.getPort().toString();
            cmd.ddnport = vo.getTunnelInterface();
            cmd.speed = vo.getBandwidthOfferingUuid();
            return cmd;
        }
    }

    public static class StartAllResponse extends VpnAgentResponse {
    }

    /**
     * 删除VPN：/vpn/destroy-vpn
     */
    public static class DestroyVpnCmd extends VpnAgentCommand {
        String vpnvlanid;
        String ddnport;
        String vpnport;
        public static DestroyVpnCmd valueOf(VpnVO vo) {
            DestroyVpnCmd cmd = new DestroyVpnCmd();
            cmd.vpnuuid = vo.getUuid();
            cmd.vpnvlanid = vo.getVlan().toString();
            cmd.vpnport = vo.getPort().toString();
            cmd.ddnport = vo.getTunnelInterface();
            return cmd;
        }
    }

    public static class DestroyVpnResponse extends VpnAgentResponse {
    }

    /**
     * 下载证书：/vpn/download-cert
     */
    public static class DownloadCertCmd extends VpnAgentCommand {

        public static DownloadCertCmd valueOf(VpnVO vo) {
            DownloadCertCmd cmd = new DownloadCertCmd();
            return cmd;
        }
    }

    public static class DownloadCertResponse extends VpnAgentResponse {
    }
}
