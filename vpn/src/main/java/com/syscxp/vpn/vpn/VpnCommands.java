package com.syscxp.vpn.vpn;

import com.syscxp.header.vpn.host.VpnHostVO;
import com.syscxp.header.vpn.vpn.VpnVO;


/**
 * VPN接口参数
 */
public class VpnCommands {
    public static class VpnAgentCommand {
        private String host_ip;
        private String uuid;
        private Integer port;
        private String public_ip;

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getHostIp() {
            return host_ip;
        }

        public void setHostIp(String hostIp) {
            this.host_ip = hostIp;
        }

        public String getVpnUuid() {
            return uuid;
        }

        public void setVpnUuid(String vpnUuid) {
            this.uuid = vpnUuid;
        }

        public String getPublicIp() {
            return public_ip;
        }

        public void setPublicIp(String publicIp) {
            this.public_ip = publicIp;
        }
    }

    public static class VpnAgentResponse {
        // 任务结果
        private TaskResult result;
        // 运行状态
        private RunStatus status;

        public RunStatus getStatus() {
            return status;
        }

        public void setStatus(RunStatus status) {
            this.status = status;
        }

        public TaskResult getResult() {
            return result;
        }

        public void setResult(TaskResult result) {
            this.result = result;
        }


    }
    public static class TaskResult {
        private String message;
        private boolean success = false;

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
     * 查询物理机状态：/vpn/agent-status
     */
    public static class CheckVpnHostStatusCmd extends VpnAgentCommand {
        public static CheckVpnHostStatusCmd valueOf(VpnHostVO vo) {
            CheckVpnHostStatusCmd cmd = new CheckVpnHostStatusCmd();
            cmd.setHostIp(vo.getHostIp());
            return cmd;
        }
    }

    public static class CheckStatusResponse extends VpnAgentResponse {
        private RunStatus status;


        public RunStatus getStatus() {
            return status;
        }

        public void setStatus(RunStatus status) {
            this.status = status;
        }
    }

    /**
     * 添加物理机：/vpn/add-host
     */
    public static class AddVpnHostCmd extends VpnAgentCommand {

        public static AddVpnHostCmd valueOf(VpnHostVO vo) {
            AddVpnHostCmd cmd = new AddVpnHostCmd();
            cmd.setPublicIp(vo.getPublicIp());
            cmd.setHostIp(vo.getHostIp());
            return cmd;
        }
    }

    public static class AddVpnHostResponse extends VpnAgentResponse {

    }

    /**
     * 删除物理机：
     */
    public static class DeleteVpnHostCmd extends VpnAgentCommand {

        public static DeleteVpnHostCmd valueOf(VpnHostVO vo) {
            DeleteVpnHostCmd cmd = new DeleteVpnHostCmd();
            cmd.setPublicIp(vo.getPublicIp());
            cmd.setHostIp(vo.getHostIp());
            return cmd;
        }
    }

    public static class DeleteVpnHostResponse extends VpnAgentResponse {

    }

    /**
     * 物理机重连：/vpn/recconnect
     */
    public static class ReconnectVpnHostCmd extends VpnAgentCommand {

        public static ReconnectVpnHostCmd valueOf(VpnHostVO vo) {
            ReconnectVpnHostCmd cmd = new ReconnectVpnHostCmd();
            cmd.setPublicIp(vo.getPublicIp());
            cmd.setHostIp(vo.getHostIp());
            return cmd;
        }
    }

    public static class ReconnectVpnHostResponse extends VpnAgentResponse {

    }

    /**
     * 查询VPN状态：/vpn/vpn-status
     */
    public static class CheckVpnStatusCmd extends VpnAgentCommand {

        public static CheckVpnStatusCmd valueOf(VpnVO vo) {
            CheckVpnStatusCmd cmd = new CheckVpnStatusCmd();
            cmd.setVpnUuid(vo.getUuid());
            cmd.setHostIp(vo.getVpnHost().getHostIp());
            cmd.setPort(vo.getPort());
            return cmd;
        }

    }

    /**
     * 创建VPN：/vpn/init-vpn
     * VPN重连:/vpn/start-vpn
     */
    public static class CreateVpnCmd extends VpnAgentCommand {
        private String cidr;
        private Long bandwidth;
        private Integer duration;

        public static CreateVpnCmd valueOf(VpnVO vo) {
            CreateVpnCmd cmd = new CreateVpnCmd();
            cmd.setHostIp(vo.getVpnHost().getHostIp());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setPublicIp(vo.getVpnHost().getPublicIp());
            cmd.setPort(vo.getPort());
            cmd.setVpnCidr(vo.getVpnCidr());
            cmd.setBandwidth(vo.getBandwidthOfferingUuid());
            cmd.setDuration(vo.getDuration());
            return cmd;
        }

        public String getVpnCidr() {
            return cidr;
        }

        public void setVpnCidr(String vpnCidr) {
            this.cidr = vpnCidr;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }
    }

    public static class CreateVpnResponse extends VpnAgentResponse {
    }


    /**
     * 关闭VPN：/vpn/close-vpn
     * 删除VPN：/vpn/destroy-vpn
     */
    public static class DeleteVpnCmd extends VpnAgentCommand{

        public static DeleteVpnCmd valueOf(VpnVO vo) {
            DeleteVpnCmd cmd = new DeleteVpnCmd();
            return cmd;
        }

    }

    public static class UpdateVpnBandWidthCmd  extends VpnCommands.VpnAgentCommand {
        private Long bandwidth;

        public static UpdateVpnBandWidthCmd valueOf(VpnVO vo) {
            UpdateVpnBandWidthCmd cmd = new UpdateVpnBandWidthCmd();
            cmd.setHostIp(vo.getVpnHost().getHostIp());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setBandwidth(vo.getBandwidthOfferingUuid());
            return cmd;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }
    }

    public static class UpdateVpnBandWidthResponse extends VpnAgentResponse {

    }

    /**
     * 修改VPN网段
     */
    public static class UpdateVpnCidrCmd extends VpnAgentCommand {
        private String cidr;
        private Long bandwidth;

        public static UpdateVpnCidrCmd valueOf(VpnVO vo) {
            UpdateVpnCidrCmd cmd = new UpdateVpnCidrCmd();
            cmd.setBandwidth(vo.getBandwidthOfferingUuid());
            cmd.setCidr(vo.getVpnCidr());
            return cmd;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public String getCidr() {
            return cidr;
        }

        public void setCidr(String cidr) {
            this.cidr = cidr;
        }
    }

    /**
     * 重置证书：/vpn/reset-cert
     */
    public static class ResetCertificateCmd extends VpnAgentCommand {
        private String cidr;
        private Long bandwidth;


        public static ResetCertificateCmd valueOf(VpnVO vo) {
            ResetCertificateCmd cmd = new ResetCertificateCmd();
            cmd.setBandwidth(vo.getBandwidthOfferingUuid());
            cmd.setCidr(vo.getVpnCidr());
            return cmd;
        }

        public String getCidr() {
            return cidr;
        }

        public void setCidr(String cidr) {
            this.cidr = cidr;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }
    }
}
