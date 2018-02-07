package com.syscxp.header.tunnel.monitor;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-13.
 * @Description: 监控agent通信.
 */
public class MonitorAgentCommands {
    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-11-02.
     * @Description: 测速命令下发.
     */
    public static class SpeedRecord{
        private String tunnel_id;
        private MonitorAgentCommands.SpeedCommandType type;
        private Integer port;
        private String src_ip;
        private Integer time;
        private String interface_name;
        private Integer vlan;

        public String getTunnel_id() {
            return tunnel_id;
        }

        public void setTunnel_id(String tunnel_id) {
            this.tunnel_id = tunnel_id;
        }

        public SpeedCommandType getType() {
            return type;
        }

        public void setType(SpeedCommandType type) {
            this.type = type;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getSrc_ip() {
            return src_ip;
        }

        public void setSrc_ip(String src_ip) {
            this.src_ip = src_ip;
        }

        public Integer getTime() {
            return time;
        }

        public void setTime(Integer time) {
            this.time = time;
        }

        public String getInterface_name() {
            return interface_name;
        }

        public void setInterface_name(String interface_name) {
            this.interface_name = interface_name;
        }

        public Integer getVlan() {
            return vlan;
        }

        public void setVlan(Integer vlan) {
            this.vlan = vlan;
        }
    }

    /**
     * 服务端命令
     */
    public static class SpeedRecordServer extends SpeedRecord{
        private String guid;

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }
    }

    /**
     * 客户端命令
     */
    public static class SpeedRecordClient extends SpeedRecord{
        private ProtocolType protocol;
        private Long bandwidth;
        private String dst_ip;

        public ProtocolType getProtocol() {
            return protocol;
        }

        public void setProtocol(ProtocolType protocol) {
            this.protocol = protocol;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public String getDst_ip() {
            return dst_ip;
        }

        public void setDst_ip(String dst_ip) {
            this.dst_ip = dst_ip;
        }
    }

    /***
     * 测速结果
     */
    public static class SpeedResult{
        private String tunnel_id;
        private boolean complete_flag;
        private float iperf_data;
        private String time_stamp;
        private boolean success;
        private String msg;

        public String getTunnel_id() {
            return tunnel_id;
        }

        public void setTunnel_id(String tunnel_id) {
            this.tunnel_id = tunnel_id;
        }

        public boolean isComplete_flag() {
            return complete_flag;
        }

        public void setComplete_flag(boolean complete_flag) {
            this.complete_flag = complete_flag;
        }

        public float getIperf_data() {
            return iperf_data;
        }

        public void setIperf_data(float iperf_data) {
            this.iperf_data = iperf_data;
        }

        public String getTime_stamp() {
            return time_stamp;
        }

        public void setTime_stamp(String time_stamp) {
            this.time_stamp = time_stamp;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    /***
     * 网络工具
     */
    public static class NettoolCommand {
        private String command;
        private String remote_ip;
        private String guid;

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getRemote_ip() {
            return remote_ip;
        }

        public void setRemote_ip(String remote_ip) {
            this.remote_ip = remote_ip;
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }
    }

    /***
     * 网络工具返回结果
     */
    public static class NettoolResult {
        private String guid;
        private String result;

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    /***
     * falcon alarm获取tunnel信息
     */
    public static class EndpointTunnel{
        private String tunnelUuid;
        private String tunnelName;
        private String endpoingAMip;
        private String endpoingZMip;
        private String nodeA;
        private String nodeZ;
        private Integer endpointAVlan;
        private Integer endpointZVlan;
        private long bandwidth;
        private String accountUuid;

        public String getTunnelUuid() {
            return tunnelUuid;
        }

        public void setTunnelUuid(String tunnelUuid) {
            this.tunnelUuid = tunnelUuid;
        }

        public String getTunnelName() {
            return tunnelName;
        }

        public void setTunnelName(String tunnelName) {
            this.tunnelName = tunnelName;
        }

        public String getNodeA() {
            return nodeA;
        }

        public void setNodeA(String nodeA) {
            this.nodeA = nodeA;
        }

        public String getNodeZ() {
            return nodeZ;
        }

        public void setNodeZ(String nodeZ) {
            this.nodeZ = nodeZ;
        }

        public Integer getEndpointAVlan() {
            return endpointAVlan;
        }

        public void setEndpointAVlan(Integer endpointAVlan) {
            this.endpointAVlan = endpointAVlan;
        }

        public Integer getEndpointZVlan() {
            return endpointZVlan;
        }

        public void setEndpointZVlan(Integer endpointZVlan) {
            this.endpointZVlan = endpointZVlan;
        }

        public long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public String getEndpoingAMip() {
            return endpoingAMip;
        }

        public void setEndpoingAMip(String endpoingAMip) {
            this.endpoingAMip = endpoingAMip;
        }

        public String getEndpoingZMip() {
            return endpoingZMip;
        }

        public void setEndpoingZMip(String endpoingZMip) {
            this.endpoingZMip = endpoingZMip;
        }

        public String getAccountUuid() {
            return accountUuid;
        }

        public void setAccountUuid(String accountUuid) {
            this.accountUuid = accountUuid;
        }
    }

    /**
     * 获取端口
     * @return
     */
    public static int getPort(){
        if(ports.isEmpty())
            initPorts();

        int port = ports.get(0);
        // 放入队列尾部
        ports.remove(0);
        ports.add(port);

        return port;
    }

    static List<Integer> ports = initPorts();

    /**
     * 初始化端口列表
     * @return
     */
    public static List initPorts(){
        List<Integer> ports = new LinkedList<>();
        for(int i=7000;i<7500;i++){
            ports.add(i);
        }

        return ports;
    }

    public enum SpeedCommandType{
        server,
        client
    }

    public static class AgentIcmp {
        private String tunnel_id;
        private String switch_mip;
        private String src_monitor_ip;
        private String dst_monitor_ip;
        private Integer vlan;
        private String interface_name;

        public String getTunnel_id() {
            return tunnel_id;
        }

        public void setTunnel_id(String tunnel_id) {
            this.tunnel_id = tunnel_id;
        }

        public String getSwitch_mip() {
            return switch_mip;
        }

        public void setSwitch_mip(String switch_mip) {
            this.switch_mip = switch_mip;
        }

        public String getSrc_monitor_ip() {
            return src_monitor_ip;
        }

        public void setSrc_monitor_ip(String src_monitor_ip) {
            this.src_monitor_ip = src_monitor_ip;
        }

        public String getDst_monitor_ip() {
            return dst_monitor_ip;
        }

        public void setDst_monitor_ip(String dst_monitor_ip) {
            this.dst_monitor_ip = dst_monitor_ip;
        }

        public Integer getVlan() {
            return vlan;
        }

        public void setVlan(Integer vlan) {
            this.vlan = vlan;
        }

        public String getInterface_name() {
            return interface_name;
        }

        public void setInterface_name(String interface_name) {
            this.interface_name = interface_name;
        }
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-11-02.
     * @Description: Agent API返回对象.
     */
    public static class RestResponse {
        private String msg;
        private boolean success;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

    /**
     * falcon-alarm 按mIp、vlan查询tunnel信息
     */
    public static class FalconGetTunnelCommand {
        private String tunnelUuid;

        public String getTunnelUuid() {
            return tunnelUuid;
        }

        public void setTunnelUuid(String tunnelUuid) {
            this.tunnelUuid = tunnelUuid;
        }
    }

    public static class FalconResponse{
        private boolean success;
        private String msg;
        private List<EndpointTunnelsInventory> inventories;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<EndpointTunnelsInventory> getInventories() {
            return inventories;
        }

        public void setInventories(List<EndpointTunnelsInventory> inventories) {
            this.inventories = inventories;
        }
    }
}
