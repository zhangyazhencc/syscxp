package org.zstack.vpn.manage;

import org.springframework.http.HttpStatus;
import org.zstack.vpn.header.vpn.VpnInterfaceVO;
import org.zstack.vpn.header.vpn.VpnVO;

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
        AgentCommand(String hostIp) {
            this.hostIp = hostIp;
        }

        private String hostIp;

        public String getHostIp() {
            return hostIp;
        }

        public void setHostIp(String hostIp) {
            this.hostIp = hostIp;
        }
    }

    public static class CheckVpnHostStateCmd extends AgentCommand {
        CheckVpnHostStateCmd(String hostIp) {
            super(hostIp);
        }
    }

    public static class CheckVpnStateCmd extends AgentCommand {
        CheckVpnStateCmd(String hostIp) {
            super(hostIp);
        }
    }

    public static class CheckStateResponse extends AgentResponse {

    }

    public static class CreateVpnCmd extends AgentCommand {
        private String vpnUuid;
        private Integer port;
        private String vpnCidr;
        private Long bandwidth;
        private Integer months;
        private List<AddVpnInterfaceCmd> vpnInterfaceCmds;

        CreateVpnCmd(String hostIp) {
            super(hostIp);
        }

        public static CreateVpnCmd valueOf(String hostIp, VpnVO vo, VpnInterfaceVO iface) {
            CreateVpnCmd cmd = new CreateVpnCmd(hostIp);
            cmd.setHostIp(vo.getHostUuid());
            cmd.setVpnUuid(vo.getUuid());
            cmd.setPort(vo.getPort());
            cmd.setVpnCidr(vo.getVpnCidr());
            cmd.setBandwidth(vo.getBandwidth());
            cmd.setMonths(vo.getMonths());
            List<AddVpnInterfaceCmd> interfaceCmds = new ArrayList<>();
            interfaceCmds.add(AddVpnInterfaceCmd.valueOf(hostIp, iface));
            cmd.setVpnInterfaceCmds(interfaceCmds);
            return cmd;
        }

        public List<AddVpnInterfaceCmd> getVpnInterfaceCmds() {
            return vpnInterfaceCmds;
        }

        public void setVpnInterfaceCmds(List<AddVpnInterfaceCmd> vpnInterfaceCmds) {
            this.vpnInterfaceCmds = vpnInterfaceCmds;
        }

        public String getVpnUuid() {
            return vpnUuid;
        }

        public void setVpnUuid(String vpnUuid) {
            this.vpnUuid = vpnUuid;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getVpnCidr() {
            return vpnCidr;
        }

        public void setVpnCidr(String vpnCidr) {
            this.vpnCidr = vpnCidr;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public Integer getMonths() {
            return months;
        }

        public void setMonths(Integer months) {
            this.months = months;
        }
    }

    public static class CreateVpnResponse extends AgentResponse {

    }

    public static class DeleteVpnCmd extends AgentCommand {
        DeleteVpnCmd(String hostIp) {
            super(hostIp);
        }
    }

    public static class DeleteVpnResponse extends AgentResponse {
    }

    public static class StopVpnCmd extends AgentCommand {
        StopVpnCmd(String hostIp) {
            super(hostIp);
        }
    }

    public static class StopVpnResponse extends AgentResponse {

    }

    public static class StartVpnCmd extends AgentCommand {
        StartVpnCmd(String hostIp) {
            super(hostIp);
        }
    }

    public static class StartVpnResponse extends AgentResponse {

    }

    public static class UpdateVpnExpireDateCmd extends AgentCommand {
        private Integer months;

        UpdateVpnExpireDateCmd(String hostIp) {
            super(hostIp);
        }

        public Integer getMonths() {
            return months;
        }

        public void setMonths(Integer months) {
            this.months = months;
        }
    }

    public static class UpdateVpnExpireDateResponse extends AgentResponse {

    }

    public static class UpdateVpnBandWidthCmd extends AgentCommand {
        private Long bandwidth;

        UpdateVpnBandWidthCmd(String hostIp) {
            super(hostIp);
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

    public static class AddVpnInterfaceCmd extends AgentCommand {
        private String localIp;
        private String netmask;
        private Integer vlan;

        AddVpnInterfaceCmd(String hostIp) {
            super(hostIp);
        }

        public String getLocalIp() {
            return localIp;
        }

        public void setLocalIp(String localIp) {
            this.localIp = localIp;
        }

        public String getNetmask() {
            return netmask;
        }

        public void setNetmask(String netmask) {
            this.netmask = netmask;
        }

        public Integer getVlan() {
            return vlan;
        }

        public void setVlan(Integer vlan) {
            this.vlan = vlan;
        }

        public static AddVpnInterfaceCmd valueOf(String hostIp, VpnInterfaceVO vo) {
            AddVpnInterfaceCmd cmd = new AddVpnInterfaceCmd(hostIp);
            cmd.setLocalIp(vo.getLocalIp());
            cmd.setNetmask(vo.getNetmask());
            cmd.setVlan(vo.getVlan());
            return cmd;
        }
    }

    public static class AddVpnInterfaceResponse extends AgentResponse {

    }

    public static class DeleteVpnInterfaceCmd extends AgentCommand {
        private String localIp;
        private String remoteIp;
        private String netmask;

        DeleteVpnInterfaceCmd(String hostIp) {
            super(hostIp);
        }

        public String getLocalIp() {
            return localIp;
        }

        public void setLocalIp(String localIp) {
            this.localIp = localIp;
        }

        public String getRemoteIp() {
            return remoteIp;
        }

        public void setRemoteIp(String remoteIp) {
            this.remoteIp = remoteIp;
        }

        public String getNetmask() {
            return netmask;
        }

        public void setNetmask(String netmask) {
            this.netmask = netmask;
        }
    }

    public static class DeleteVpnInterfaceResponse extends AgentResponse {

    }

    public static class AddVpnRouteCmd extends AgentCommand {
        private String routeType;
        private String nextIface;
        private String targetCidr;

        AddVpnRouteCmd(String hostIp) {
            super(hostIp);
        }

        public String getRouteType() {
            return routeType;
        }

        public void setRouteType(String routeType) {
            this.routeType = routeType;
        }

        public String getNextIface() {
            return nextIface;
        }

        public void setNextIface(String nextIface) {
            this.nextIface = nextIface;
        }

        public String getTargetCidr() {
            return targetCidr;
        }

        public void setTargetCidr(String targetCidr) {
            this.targetCidr = targetCidr;
        }
    }

    public static class AddVpnRouteResponse extends AgentResponse {

    }

    public static class DeleteVpnRouteCmd extends AgentCommand {
        private String routeType;
        private String nextIface;
        private String targetCidr;

        DeleteVpnRouteCmd(String hostIp) {
            super(hostIp);
        }

        public String getRouteType() {
            return routeType;
        }

        public void setRouteType(String routeType) {
            this.routeType = routeType;
        }

        public String getNextIface() {
            return nextIface;
        }

        public void setNextIface(String nextIface) {
            this.nextIface = nextIface;
        }

        public String getTargetCidr() {
            return targetCidr;
        }

        public void setTargetCidr(String targetCidr) {
            this.targetCidr = targetCidr;
        }
    }

    public static class DeleteVpnRouteResponse extends AgentResponse {

    }

}
