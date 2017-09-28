package org.zstack.vpn.vpn;

import org.zstack.header.vpn.VpnAgentCommand;
import org.zstack.header.vpn.VpnAgentResponse;
import org.zstack.utils.CollectionUtils;
import org.zstack.utils.function.Function;
import org.zstack.vpn.header.host.VpnHostVO;
import org.zstack.vpn.header.vpn.*;

import java.util.ArrayList;
import java.util.List;

public class VpnCommands {
    
    public static class CheckVpnHostStatusCmd extends VpnAgentCommand {
        public static CheckVpnHostStatusCmd valueOf(VpnHostVO vo) {
            CheckVpnHostStatusCmd cmd = new CheckVpnHostStatusCmd();
            cmd.setHostIp(vo.getManageIp());
            return cmd;
        }
    }

    public static class AddVpnHostCmd extends VpnAgentCommand {

        public static AddVpnHostCmd valueOf(VpnHostVO vo) {
            AddVpnHostCmd cmd = new AddVpnHostCmd();
            cmd.setHostIp(vo.getManageIp());
            return cmd;
        }
    }
    public static class AddVpnHostResponse extends VpnAgentResponse {

    }
    public static class ReconnectVpnHostCmd extends VpnAgentCommand {

        public static ReconnectVpnHostCmd valueOf(VpnHostVO vo) {
            ReconnectVpnHostCmd cmd = new ReconnectVpnHostCmd();
            cmd.setHostIp(vo.getManageIp());
            return cmd;
        }
    }
    public static class ReconnectVpnHostResponse extends VpnAgentResponse {

    }

    public static class CheckVpnStatusCmd extends VpnAgentCommand {

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
    public static class CheckStatusResponse extends VpnAgentResponse {

    }


    public static class CreateVpnCmd extends VpnAgentCommand {
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

    public static class CreateVpnResponse extends VpnAgentResponse {

    }

    public static class UpdateVpnStateCmd extends VpnAgentCommand {
        private List<String> vlan_list;

        public static UpdateVpnStateCmd valueOf(VpnVO vo) {
            UpdateVpnStateCmd cmd = new UpdateVpnStateCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setVlans(CollectionUtils.transformToList(vo.getVpnInterfaces(), new Function<String, VpnInterfaceVO>() {
                @Override
                public String call(VpnInterfaceVO arg) {
                    return arg.getVlan();
                }
            }));
            return cmd;
        }

        public List<String> getVlans() {
            return vlan_list;
        }

        public void setVlans(List<String> vlans) {
            this.vlan_list = vlans;
        }
    }

    public static class UpdateVpnStateResponse extends VpnAgentResponse {

    }
    public static class DeleteVpnCmd extends VpnAgentCommand {
        private List<String> vlan_list;

        public static DeleteVpnCmd valueOf(VpnVO vo) {
            DeleteVpnCmd cmd = new DeleteVpnCmd();
            cmd.setHostIp(vo.getVpnHost().getManageIp());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setVlans(CollectionUtils.transformToList(vo.getVpnInterfaces(), new Function<String, VpnInterfaceVO>() {
                @Override
                public String call(VpnInterfaceVO arg) {
                    return arg.getVlan();
                }
            }));
            return cmd;
        }

        public List<String> getVlans() {
            return vlan_list;
        }

        public void setVlans(List<String> vlans) {
            this.vlan_list = vlans;
        }
    }

    public static class DeleteVpnResponse extends VpnAgentResponse {

    }

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

    public static class UpdateVpnCidrCmd extends VpnAgentCommand {
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

    public static class UpdateVpnCidrResponse extends VpnAgentResponse {

    }

    public static class VpnInterfaceCmd extends VpnAgentCommand {
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

    public static class VpnInterfaceResponse extends VpnAgentResponse {

    }

    public static class VpnRouteCmd extends VpnAgentCommand {
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

    public static class VpnRouteResponse extends VpnAgentResponse {

    }

}
