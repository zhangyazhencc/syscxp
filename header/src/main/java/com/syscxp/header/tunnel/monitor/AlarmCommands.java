package com.syscxp.header.tunnel.monitor;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-02.
 * @Description: Tunnel For 告警.
 */
public class AlarmCommands {
    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2018-05-02.
     * @Description: 三层网络监控.
     */
    public static class L3NetworkMonitorCommand {
        private String l3NetworkUuid;
        private String l3NetworkName;
        private String ownerAccountUuid;
        private String interfaceNameA;
        private String interfaceNameZ;
        private String endpointAIp;
        private String endpointZIp;
        private Integer endpointAVid;
        private Integer endpointZVid;
        private long endpointABandwidth;
        private long endpointZBandwidth;
        private String monitorUuid;

        public String getL3NetworkUuid() {
            return l3NetworkUuid;
        }

        public void setL3NetworkUuid(String l3NetworkUuid) {
            this.l3NetworkUuid = l3NetworkUuid;
        }

        public String getL3NetworkName() {
            return l3NetworkName;
        }

        public void setL3NetworkName(String l3NetworkName) {
            this.l3NetworkName = l3NetworkName;
        }

        public String getOwnerAccountUuid() {
            return ownerAccountUuid;
        }

        public void setOwnerAccountUuid(String ownerAccountUuid) {
            this.ownerAccountUuid = ownerAccountUuid;
        }

        public String getInterfaceNameA() {
            return interfaceNameA;
        }

        public void setInterfaceNameA(String interfaceNameA) {
            this.interfaceNameA = interfaceNameA;
        }

        public String getInterfaceNameZ() {
            return interfaceNameZ;
        }

        public void setInterfaceNameZ(String interfaceNameZ) {
            this.interfaceNameZ = interfaceNameZ;
        }

        public String getEndpointAIp() {
            return endpointAIp;
        }

        public void setEndpointAIp(String endpointAIp) {
            this.endpointAIp = endpointAIp;
        }

        public String getEndpointZIp() {
            return endpointZIp;
        }

        public void setEndpointZIp(String endpointZIp) {
            this.endpointZIp = endpointZIp;
        }

        public Integer getEndpointAVid() {
            return endpointAVid;
        }

        public void setEndpointAVid(Integer endpointAVid) {
            this.endpointAVid = endpointAVid;
        }

        public Integer getEndpointZVid() {
            return endpointZVid;
        }

        public void setEndpointZVid(Integer endpointZVid) {
            this.endpointZVid = endpointZVid;
        }

        public String getMonitorUuid() {
            return monitorUuid;
        }

        public void setMonitorUuid(String monitorUuid) {
            this.monitorUuid = monitorUuid;
        }

        public long getEndpointABandwidth() {
            return endpointABandwidth;
        }

        public void setEndpointABandwidth(long endpointABandwidth) {
            this.endpointABandwidth = endpointABandwidth;
        }

        public long getEndpointZBandwidth() {
            return endpointZBandwidth;
        }

        public void setEndpointZBandwidth(long endpointZBandwidth) {
            this.endpointZBandwidth = endpointZBandwidth;
        }
    }
}
