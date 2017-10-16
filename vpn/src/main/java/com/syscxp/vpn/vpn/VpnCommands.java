package com.syscxp.vpn.vpn;

import com.syscxp.header.vpn.VpnAgentCommand;
import com.syscxp.header.vpn.VpnAgentResponse;
import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.function.Function;
import com.syscxp.vpn.header.host.VpnHostVO;
import com.syscxp.vpn.header.vpn.VpnInterfaceVO;
import com.syscxp.vpn.header.vpn.VpnRouteVO;
import com.syscxp.vpn.header.vpn.VpnVO;
import com.syscxp.vpn.header.vpn.*;

import java.util.ArrayList;
import java.util.List;

/**
 * VPN接口参数
 */
public class VpnCommands {
    /**
     * 查询物理机状态：/vpn/agent-status
     */
    public static class CheckVpnHostStatusCmd extends VpnAgentCommand {
        public static CheckVpnHostStatusCmd valueOf(VpnHostVO vo) {
            CheckVpnHostStatusCmd cmd = new CheckVpnHostStatusCmd();
            cmd.setHostIp(vo.getManageIp());
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
            cmd.setHostIp(vo.getManageIp());
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
            cmd.setHostIp(vo.getManageIp());
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
            cmd.setHostIp(vo.getManageIp());
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
            cmd.setHostIp(vo.getVpnHost().getManageIp());
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
        private List<VpnInterfaceCmd> ddn_if_list;
        private List<VpnRouteCmd> route_list;

        public static CreateVpnCmd valueOf(VpnVO vo) {
            CreateVpnCmd cmd = new CreateVpnCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setPublicIp(vo.getVpnHost().getPublicIp());
            cmd.setPort(vo.getPort());
            cmd.setVpnCidr(vo.getVpnCidr());
            cmd.setBandwidth(vo.getBandwidth());
            cmd.setDuration(vo.getDuration());
            cmd.setVpnInterfaceCmds(VpnInterfaceCmd.valueOf(cmd.getHostIp(), vo.getVpnInterfaces()));
            cmd.setVpnRouteCmds(VpnRouteCmd.valueOf(cmd.getHostIp(), vo.getVpnRoutes()));
            return cmd;
        }

        public List<VpnInterfaceCmd> getVpnInterfaceCmds() {
            return ddn_if_list;
        }

        public void setVpnInterfaceCmds(List<VpnInterfaceCmd> vpnInterfaceCmds) {
            this.ddn_if_list = vpnInterfaceCmds;
        }

        public List<VpnRouteCmd> getVpnRouteCmds() {
            return route_list;
        }

        public void setVpnRouteCmds(List<VpnRouteCmd> vpnRouteCmds) {
            this.route_list = vpnRouteCmds;
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
    public static class DeleteVpnCmd extends VpnAgentCommand {
        private List<VpnInterfaceCmd> vlan_list;

        public static DeleteVpnCmd valueOf(VpnVO vo) {
            DeleteVpnCmd cmd = new DeleteVpnCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setVpnInterfaceCmds(VpnInterfaceCmd.valueOf(cmd.getHostIp(), vo.getVpnInterfaces()));
            return cmd;
        }

        public List<VpnInterfaceCmd> getVpnInterfaceCmds() {
            return vlan_list;
        }

        public void setVpnInterfaceCmds(List<VpnInterfaceCmd> vpnInterfaceCmds) {
            this.vlan_list = vpnInterfaceCmds;
        }

    }

    public static class DeleteVpnResponse extends VpnAgentResponse {

    }

    /**
     * 修改VPN带宽：
     */
    public static class UpdateVpnBandWidthCmd extends VpnAgentCommand {
        private Long bandwidth;

        public static UpdateVpnBandWidthCmd valueOf(VpnVO vo) {
            UpdateVpnBandWidthCmd cmd = new UpdateVpnBandWidthCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setBandwidth(vo.getBandwidth());
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
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setBandwidth(vo.getBandwidth());
            cmd.setCidr(vo.getVpnCidr());
            cmd.setPort(vo.getPort());
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

    public static class UpdateVpnCidrResponse extends VpnAgentResponse {

    }

    /**
     * 添加VPN接口：/vpn/add-ddn-if
     * 删除VPN接口：/vpn/del-ddn-if
     */
    public static class VpnInterfaceCmd extends VpnAgentCommand {
        private String interface_name;
        private String local_ip;
        private String netmask;
        private String vlan;

        public String getName() {
            return interface_name;
        }

        public void setName(String name) {
            this.interface_name = name;
        }

        public String getLocalIp() {
            return local_ip;
        }

        public void setLocalIp(String localIp) {
            this.local_ip = localIp;
        }

        public String getNetmask() {
            return netmask;
        }

        public void setNetmask(String netmask) {
            this.netmask = netmask;
        }

        public String getVlan() {
            return vlan;
        }

        public void setVlan(String vlan) {
            this.vlan = vlan;
        }

        public static VpnInterfaceCmd valueOf(String hostIp, VpnInterfaceVO vo) {
            VpnInterfaceCmd cmd = new VpnInterfaceCmd();
            cmd.setHostIp(hostIp);
            cmd.setName(vo.getName());
            cmd.setVpnUuid(vo.getVpnUuid());
            cmd.setLocalIp(vo.getLocalIp());
            cmd.setNetmask(vo.getNetmask());
            cmd.setVlan(vo.getVlan());
            return cmd;
        }

        public static List<VpnInterfaceCmd> valueOf(String hostIp, List<VpnInterfaceVO> vos) {
            List<VpnInterfaceCmd> cmds = new ArrayList<>();
            vos.forEach(vo -> cmds.add(VpnInterfaceCmd.valueOf(hostIp, vo)));
            return cmds;
        }
    }

    public static class VpnInterfaceResponse extends VpnAgentResponse {

    }

    /**
     * 添加VPN路由：/vpn/add-route
     * 删除VPN路由：/vpn/del-route
     */
    public static class VpnRouteCmd extends VpnAgentCommand {
        private List<String> next_ip;
        private String dest_cidr;

        public static VpnRouteCmd valueOf(String hostIp, VpnRouteVO vo) {
            VpnRouteCmd cmd = new VpnRouteCmd();
            cmd.setHostIp(hostIp);
            cmd.setVpnUuid(vo.getVpnUuid());
            cmd.setNextIface(vo.getNextInterface());
            cmd.setTargetCidr(vo.getTargetCidr());
            return cmd;
        }

        public static List<VpnRouteCmd> valueOf(String hostIp, List<VpnRouteVO> vos) {
            List<VpnRouteCmd> cmds = new ArrayList<>();
            vos.forEach(vo -> cmds.add(VpnRouteCmd.valueOf(hostIp, vo)));
            return cmds;
        }

        public List<String> getNextIface() {
            return next_ip;
        }

        public void setNextIface(List<String> nextIface) {
            this.next_ip = nextIface;
        }

        public String getTargetCidr() {
            return dest_cidr;
        }

        public void setTargetCidr(String targetCidr) {
            this.dest_cidr = targetCidr;
        }
    }

    public static class VpnRouteResponse extends VpnAgentResponse {

    }

    /**
     * 下载client证书：/vpn/client-conf
     */
    public static class DownloadCertificateCmd extends VpnAgentCommand {


        public static DownloadCertificateCmd valueOf(VpnVO vo) {
            DownloadCertificateCmd cmd = new DownloadCertificateCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setPublicIp(vo.getVpnHost().getPublicIp());
            cmd.setVpnUuid(vo.getUuid());
            return cmd;
        }

    }

    public static class DownloadCertificateResponse extends VpnAgentResponse {
        private String client_cert;
        private String client_key;
        private String ca_cert;
        private String client_conf;

        public String getClientCert() {
            return client_cert;
        }

        public void setClientCert(String clientCert) {
            this.client_cert = clientCert;
        }

        public String getClientKey() {
            return client_key;
        }

        public void setClientKey(String clientKey) {
            this.client_key = clientKey;
        }

        public String getCaCert() {
            return ca_cert;
        }

        public void setCaCert(String caCert) {
            this.ca_cert = caCert;
        }

        public String getClientConf() {
            return client_conf;
        }

        public void setClientConf(String clientConf) {
            this.client_conf = clientConf;
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
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setPort(vo.getPort());
            cmd.setBandwidth(vo.getBandwidth());
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

    public static class ResetCertificateResponse extends VpnAgentResponse {

    }

}
