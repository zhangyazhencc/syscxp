package org.zstack.vpn.manage;

import org.zstack.header.agent.AgentResponse;
import org.zstack.header.agent.AgentCommand;

public class VpnCommands {


    public static class CheckVpnHostStateCmd extends AgentCommand{
    }

    public static class CheckVpnHostStateResponse extends AgentResponse {

    }
    public static class CreateVpnCmd extends AgentCommand{
        private Long bandwidth;
        private String vpnCidr;
        private Integer months;
        private String hostIp;
        private String vpnUuid;

        public String getHostIp() {
            return hostIp;
        }

        public void setHostIp(String hostIp) {
            this.hostIp = hostIp;
        }

        public String getVpnUuid() {
            return vpnUuid;
        }

        public void setVpnUuid(String vpnUuid) {
            this.vpnUuid = vpnUuid;
        }
        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public String getVpnCidr() {
            return vpnCidr;
        }

        public void setVpnCidr(String vpnCidr) {
            this.vpnCidr = vpnCidr;
        }

        public Integer getMonths() {
            return months;
        }

        public void setMonths(Integer months) {
            this.months = months;
        }
    }

    public static class CreateVpnResponse extends AgentResponse{

    }
    public static class DeleteVpnCmd extends AgentCommand{
    }

    public static class DeleteVpnResponse extends AgentResponse{
    }

    public static class StopVpnCmd extends AgentCommand{
    }

    public static class StopVpnResponse extends AgentResponse{

    }
    public static class StartVpnCmd extends AgentCommand{
    }

    public static class StartVpnResponse extends AgentResponse{

    }
    public static class CheckVpnStateCmd extends AgentCommand{
    }

    public static class CheckVpnStateResponse extends AgentResponse{

    }
    public static class UpdateVpnExpireDateCmd extends AgentCommand{
        private Integer months;

        public Integer getMonths() {
            return months;
        }

        public void setMonths(Integer months) {
            this.months = months;
        }
    }

    public static class UpdateVpnExpireDateResponse extends AgentResponse{

    }
    public static class UpdateVpnBandWidthCmd extends AgentCommand{
        private Long bandwidth;

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }
    }

    public static class UpdateVpnBandWidthResponse extends AgentResponse{

    }

    public static class AddVpnInterfaceCmd extends AgentCommand{
        private String localIp;
        private String remoteIp;
        private String netmask;

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

    public static class AddVpnInterfaceResponse extends AgentResponse{

    }
    public static class DeleteVpnInterfaceCmd extends AgentCommand{
        private String localIp;
        private String remoteIp;
        private String netmask;

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

    public static class DeleteVpnInterfaceResponse extends AgentResponse{

    }

    public static class AddVpnRouteCmd extends AgentCommand{
        private String routeType;
        private String nextIface;
        private String targetCidr;

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

    public static class AddVpnRouteResponse extends AgentResponse{

    }
    public static class DeleteVpnRouteCmd extends AgentCommand{
        private String routeType;
        private String nextIface;
        private String targetCidr;

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

    public static class DeleteVpnRouteResponse extends AgentResponse{

    }

}
