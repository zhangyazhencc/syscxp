package com.syscxp.header.tunnel.monitor;

import java.util.LinkedList;
import java.util.List;

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
    public static class L3NetworkMonitors {
        private String l3NetworkUuid;
        private String name;
        private String ownerAccountUuid;
        private String srcL3EndpointUuid;
        private String srcL3EndpointName;
        private String dstL3EndpointUuid;
        private String dstL3EndpointName;
        private String monitorDetailUuid;

        public String getL3NetworkUuid() {
            return l3NetworkUuid;
        }

        public void setL3NetworkUuid(String l3NetworkUuid) {
            this.l3NetworkUuid = l3NetworkUuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOwnerAccountUuid() {
            return ownerAccountUuid;
        }

        public void setOwnerAccountUuid(String ownerAccountUuid) {
            this.ownerAccountUuid = ownerAccountUuid;
        }

        public String getSrcL3EndpointUuid() {
            return srcL3EndpointUuid;
        }

        public void setSrcL3EndpointUuid(String srcL3EndpointUuid) {
            this.srcL3EndpointUuid = srcL3EndpointUuid;
        }

        public String getSrcL3EndpointName() {
            return srcL3EndpointName;
        }

        public void setSrcL3EndpointName(String srcL3EndpointName) {
            this.srcL3EndpointName = srcL3EndpointName;
        }

        public String getDstL3EndpointUuid() {
            return dstL3EndpointUuid;
        }

        public void setDstL3EndpointUuid(String dstL3EndpointUuid) {
            this.dstL3EndpointUuid = dstL3EndpointUuid;
        }

        public String getDstL3EndpointName() {
            return dstL3EndpointName;
        }

        public void setDstL3EndpointName(String dstL3EndpointName) {
            this.dstL3EndpointName = dstL3EndpointName;
        }

        public String getMonitorDetailUuid() {
            return monitorDetailUuid;
        }

        public void setMonitorDetailUuid(String monitorDetailUuid) {
            this.monitorDetailUuid = monitorDetailUuid;
        }
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2018-05-02.
     * @Description: 三层网络监控.
     */
    public static class L3NetworkMonitorDetail {
        private String l3NetworkUuid;
        private String name;
        private String ownerAccountUuid;
        private String interfaceNameA;
        private String interfaceNameZ;
        private String endpointAIp;
        private String endpointZIp;
        private String endpointAVid;
        private String endpointZVid;
        private String monitorDetailUuid;

        public String getL3NetworkUuid() {
            return l3NetworkUuid;
        }

        public void setL3NetworkUuid(String l3NetworkUuid) {
            this.l3NetworkUuid = l3NetworkUuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getEndpointAVid() {
            return endpointAVid;
        }

        public void setEndpointAVid(String endpointAVid) {
            this.endpointAVid = endpointAVid;
        }

        public String getEndpointZVid() {
            return endpointZVid;
        }

        public void setEndpointZVid(String endpointZVid) {
            this.endpointZVid = endpointZVid;
        }

        public String getMonitorDetailUuid() {
            return monitorDetailUuid;
        }

        public void setMonitorDetailUuid(String monitorDetailUuid) {
            this.monitorDetailUuid = monitorDetailUuid;
        }
    }
}
