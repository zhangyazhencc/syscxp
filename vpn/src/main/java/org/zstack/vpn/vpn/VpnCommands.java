package org.zstack.vpn.vpn;

import org.springframework.http.HttpStatus;
import org.zstack.vpn.header.host.VpnHostVO;
import org.zstack.vpn.header.vpn.*;

import java.util.ArrayList;
import java.util.List;

public class VpnCommands {
    public static class AgentResponse {
        private String error;
        private boolean success = true;
        private HttpStatus statusCode;

        public HttpStatus getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(HttpStatus statusCode) {
            this.statusCode = statusCode;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

    public static class AgentCommand {

        private String host_ip;
        private String uuid;

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
    }


    public static class CheckVpnHostStatusCmd extends AgentCommand {
        public static CheckVpnHostStatusCmd valueOf(VpnHostVO vo) {
            CheckVpnHostStatusCmd cmd = new CheckVpnHostStatusCmd();
            cmd.setHostIp(vo.getManageIp());
            return cmd;
        }
    }

    public static class AddVpnHostCmd extends AgentCommand {

        public static AddVpnHostCmd valueOf(VpnHostVO vo) {
            AddVpnHostCmd cmd = new AddVpnHostCmd();
            cmd.setHostIp(vo.getManageIp());
            return cmd;
        }
    }
    public static class AddVpnHostResponse extends AgentResponse {

    }
    public static class ReconnectVpnHostCmd extends AgentCommand {

        public static ReconnectVpnHostCmd valueOf(VpnHostVO vo) {
            ReconnectVpnHostCmd cmd = new ReconnectVpnHostCmd();
            cmd.setHostIp(vo.getManageIp());
            return cmd;
        }
    }
    public static class ReconnectVpnHostResponse extends AgentResponse {

    }

    public static class CheckVpnStatusCmd extends AgentCommand {

        private Integer port;

        public static CheckVpnStatusCmd valueOf(VpnVO vo) {
            CheckVpnStatusCmd cmd = new CheckVpnStatusCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setPort(vo.getPort());
            return cmd;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }
    public static class CheckStatusResponse extends AgentResponse {

        private ResponseStatus status;

        public ResponseStatus getState() {
            return status;
        }

        public void setState(ResponseStatus state) {
            this.status = state;
        }

    }


    public static class CreateVpnCmd extends AgentCommand {
        private Integer port;
        private String cidr;
        private Long bandwidth;
        private Integer duration;
        private List<VpnInterfaceCmd> ddn_if_list;
        private List<VpnRouteCmd> vpnRouteCmds;

        public static CreateVpnCmd valueOf(VpnVO vo) {
            CreateVpnCmd cmd = new CreateVpnCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
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
            return vpnRouteCmds;
        }

        public void setVpnRouteCmds(List<VpnRouteCmd> vpnRouteCmds) {
            this.vpnRouteCmds = vpnRouteCmds;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
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

    public static class CreateVpnResponse extends AgentResponse {

    }

    public static class UpdateVpnStateCmd extends AgentCommand {
        private List<String> vlan_list;

        public static UpdateVpnStateCmd valueOf(VpnVO vo) {
            UpdateVpnStateCmd cmd = new UpdateVpnStateCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
            List<String> vlans = new ArrayList<>();
            vo.getVpnInterfaces().forEach(iface->{
                        vlans.add(iface.getVlan());
                    }
            );
            cmd.setVlans(vlans);
            return cmd;
        }

        public List<String> getVlans() {
            return vlan_list;
        }

        public void setVlans(List<String> vlans) {
            this.vlan_list = vlans;
        }
    }

    public static class UpdateVpnStateResponse extends AgentResponse {

    }
    public static class DeleteVpnCmd extends AgentCommand {
        private List<String> vlan_list;

        public static DeleteVpnCmd valueOf(VpnVO vo) {
            DeleteVpnCmd cmd = new DeleteVpnCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
            List<String> vlans = new ArrayList<>();
                    vo.getVpnInterfaces().forEach(iface->{
                        vlans.add(iface.getVlan());
                    }
            );
            cmd.setVlans(vlans);
            return cmd;
        }

        public List<String> getVlans() {
            return vlan_list;
        }

        public void setVlans(List<String> vlans) {
            this.vlan_list = vlans;
        }
    }

    public static class DeleteVpnResponse extends AgentResponse {

    }

    public static class UpdateVpnBandWidthCmd extends AgentCommand {
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

    public static class UpdateVpnBandWidthResponse extends AgentResponse {

    }

    public static class UpdateVpnCidrCmd extends AgentCommand {
        private String vpnCidr;

        public static UpdateVpnCidrCmd valueOf(VpnVO vo) {
            UpdateVpnCidrCmd cmd = new UpdateVpnCidrCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setVpnCidr(vo.getVpnCidr());
            return cmd;
        }

        public String getVpnCidr() {
            return vpnCidr;
        }

        public void setVpnCidr(String vpnCidr) {
            this.vpnCidr = vpnCidr;
        }
    }

    public static class UpdateVpnCidrResponse extends AgentResponse {

    }

    public static class VpnInterfaceCmd extends AgentCommand {
        private String local_ip;
        private String netmask;
        private String vlan;

        VpnInterfaceCmd(String hostIp) {
            this.setHostIp(hostIp);
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
            VpnInterfaceCmd cmd = new VpnInterfaceCmd(hostIp);
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

    public static class VpnInterfaceResponse extends AgentResponse {

    }

    public static class VpnRouteCmd extends AgentCommand {
        private List<String> next_ip;
        private String dest_cidr;

        public static VpnRouteCmd valueOf(String hostIp, VpnRouteVO vo) {
            VpnRouteCmd cmd = new VpnRouteCmd(hostIp);
            cmd.setNextIface(vo.getNextInterface());
            cmd.setTargetCidr(vo.getTargetCidr());
            return cmd;
        }

        public static List<VpnRouteCmd> valueOf(String hostIp, List<VpnRouteVO> vos) {
            List<VpnRouteCmd> cmds = new ArrayList<>();
            vos.forEach(vo -> cmds.add(VpnRouteCmd.valueOf(hostIp, vo)));
            return cmds;
        }

        VpnRouteCmd(String hostIp) {
            this.setHostIp(hostIp);
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

    public static class VpnRouteResponse extends AgentResponse {

    }

}
