package org.zstack.vpn.vpn;

import org.springframework.http.HttpStatus;
import org.zstack.vpn.header.vpn.*;

import java.util.ArrayList;
import java.util.List;

public class VpnCommands {
    public static class AgentResponse {
        private String error;
        private boolean success = true;

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
        AgentCommand(String hostIp) {
            this.host_ip = hostIp;
        }

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

    public static class CheckVpnHostStateCmd extends AgentCommand {
        CheckVpnHostStateCmd(String hostIp) {
            super(hostIp);
        }

        public static CheckVpnHostStateCmd valueOf(String hostIp) {
            return new CheckVpnHostStateCmd(hostIp);
        }
    }

    public static class CheckVpnStateCmd extends AgentCommand {
        CheckVpnStateCmd(String hostIp) {
            super(hostIp);
        }

        private Integer port;

        public static CheckVpnStateCmd valueOf(String hostIp, VpnVO vo) {
            CheckVpnStateCmd cmd = new CheckVpnStateCmd(hostIp);
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

    public static class CheckStateResponse extends AgentResponse {
        private HttpStatus statusCode;
        private VpnCreateState state;

        public VpnCreateState getState() {
            return state;
        }

        public void setState(VpnCreateState state) {
            this.state = state;
        }

        public HttpStatus getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(HttpStatus statusCode) {
            this.statusCode = statusCode;
        }
    }

    public static class CreateVpnCmd extends AgentCommand {
        private Integer port;
        private String cidr;
        private Long bandwidth;
        private Integer duration;
        private List<VpnInterfaceCmd> ddn_if_list;
        private List<VpnRouteCmd> vpnRouteCmds;

        CreateVpnCmd(String hostIp) {
            super(hostIp);
        }

        public static CreateVpnCmd valueOf(String hostIp, VpnVO vo) {
            CreateVpnCmd cmd = new CreateVpnCmd(hostIp);
            cmd.setVpnUuid(vo.getUuid());
            cmd.setPort(vo.getPort());
            cmd.setVpnCidr(vo.getVpnCidr());
            cmd.setBandwidth(vo.getBandwidth());
            cmd.setDuration(vo.getDuration());
            cmd.setVpnInterfaceCmds(VpnInterfaceCmd.valueOf(hostIp, vo.getVpnInterfaces()));
            cmd.setVpnRouteCmds(VpnRouteCmd.valueOf(hostIp, vo.getVpnRoutes()));
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
        UpdateVpnStateCmd(String hostIp) {
            super(hostIp);
        }
        private List<String> vlan_list;

        public static UpdateVpnStateCmd valueOf(String hostIp, VpnVO vo) {
            UpdateVpnStateCmd cmd = new UpdateVpnStateCmd(hostIp);
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
        DeleteVpnCmd(String hostIp) {
            super(hostIp);
        }
        private List<String> vlan_list;

        public static DeleteVpnCmd valueOf(String hostIp, VpnVO vo) {
            DeleteVpnCmd cmd = new DeleteVpnCmd(hostIp);
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

    public static class UpdateVpnDurationCmd extends AgentCommand {
        private Integer duration;

        UpdateVpnDurationCmd(String hostIp) {
            super(hostIp);
        }

        public static UpdateVpnDurationCmd valueOf(String hostIp, VpnVO vo) {
            UpdateVpnDurationCmd cmd = new UpdateVpnDurationCmd(hostIp);
            cmd.setVpnUuid(vo.getUuid());
            cmd.setDuration(vo.getDuration());
            return cmd;
        }
        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }
    }

    public static class UpdateVpnDurationResponse extends AgentResponse {

    }

    public static class UpdateVpnBandWidthCmd extends AgentCommand {
        private Long bandwidth;

        UpdateVpnBandWidthCmd(String hostIp) {
            super(hostIp);
        }

        public static UpdateVpnBandWidthCmd valueOf(String hostIp, VpnVO vo) {
            UpdateVpnBandWidthCmd cmd = new UpdateVpnBandWidthCmd(hostIp);
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

        UpdateVpnCidrCmd(String hostIp) {
            super(hostIp);
        }

        public static UpdateVpnCidrCmd valueOf(String hostIp, VpnVO vo) {
            UpdateVpnCidrCmd cmd = new UpdateVpnCidrCmd(hostIp);
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
            super(hostIp);
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
            super(hostIp);
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
