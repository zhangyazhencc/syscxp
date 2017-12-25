package com.syscxp.tunnel.sdnController;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-10-16.
 * @Description: 控制器下发命令.
 */
public class ControllerCommands {
    /**
     * 启动/停止监控命令
     */
    public static class TunnelMonitorCommand {
        private String tunnel_id;
        private List<TunnelMonitorSdn> sdn_switches;
        private List<TunnelMonitorMpls> mpls_switches;

        /**
         *
         * @param sdnConfigList sdn交换机命令
         * @param mplsConfigList mpls交换机命令
         * @return 启动监控机命令
         */
        public static TunnelMonitorCommand valueOf(String tunnel_id,List<TunnelMonitorSdn> sdnConfigList, List<TunnelMonitorMpls> mplsConfigList) {

            TunnelMonitorCommand tunnelMonitorCmd = new TunnelMonitorCommand();
            tunnelMonitorCmd.setSdn_switches(sdnConfigList);
            tunnelMonitorCmd.setMpls_switches(mplsConfigList);
            tunnelMonitorCmd.setTunnel_id(tunnel_id);

            return tunnelMonitorCmd;
        }

        public List<TunnelMonitorSdn> getSdn_switches() {
            return sdn_switches;
        }

        public void setSdn_switches(List<TunnelMonitorSdn> sdn_switches) {
            this.sdn_switches = sdn_switches;
        }

        public List<TunnelMonitorMpls> getMpls_switches() {
            return mpls_switches;
        }

        public void setMpls_switches(List<TunnelMonitorMpls> mpls_switches) {
            this.mpls_switches = mpls_switches;
        }

        public String getTunnel_id() {
            return tunnel_id;
        }

        public void setTunnel_id(String tunnel_id) {
            this.tunnel_id = tunnel_id;
        }
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-09-26.
     * @Description: RYU 控制器监控下发信息.
     */
    public static class TunnelMonitorMpls {

        private String switch_type;
        private String sub_type;
        private String port_name;
        private Integer vlan_id;
        private String m_ip;
        private String username;
        private String password;
        private Long bandwidth;
        private Integer vni;

        public String getSwitch_type() {
            return switch_type;
        }

        public void setSwitch_type(String switch_type) {
            this.switch_type = switch_type;
        }

        public String getSub_type() {
            return sub_type;
        }

        public void setSub_type(String sub_type) {
            this.sub_type = sub_type;
        }

        public String getPort_name() {
            return port_name;
        }

        public void setPort_name(String port_name) {
            this.port_name = port_name;
        }

        public Integer getVlan_id() {
            return vlan_id;
        }

        public void setVlan_id(Integer vlan_id) {
            this.vlan_id = vlan_id;
        }

        public String getM_ip() {
            return m_ip;
        }

        public void setM_ip(String m_ip) {
            this.m_ip = m_ip;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public Integer getVni() {
            return vni;
        }

        public void setVni(Integer vni) {
            this.vni = vni;
        }
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-09-26.
     * @Description: RYU 控制器监控下发信息.
     */
    public static class TunnelMonitorSdn {

        private Integer vlan_id;
        private String m_ip;
        private String in_port;
        private String nw_src;
        private String nw_dst;
        private String uplink;
        private Long bandwidth;

        public Integer getVlan_id() {
            return vlan_id;
        }

        public void setVlan_id(Integer vlan_id) {
            this.vlan_id = vlan_id;
        }

        public String getM_ip() {
            return m_ip;
        }

        public void setM_ip(String m_ip) {
            this.m_ip = m_ip;
        }

        public String getIn_port() {
            return in_port;
        }

        public void setIn_port(String in_port) {
            this.in_port = in_port;
        }

        public String getNw_src() {
            return nw_src;
        }

        public void setNw_src(String nw_src) {
            this.nw_src = nw_src;
        }

        public String getNw_dst() {
            return nw_dst;
        }

        public void setNw_dst(String nw_dst) {
            this.nw_dst = nw_dst;
        }

        public String getUplink() {
            return uplink;
        }

        public void setUplink(String uplink) {
            this.uplink = uplink;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-10-11.
     * @Description: RYU控制器返回.
     */
    public static class ControllerRestResponse {
        private boolean success;
        private String msg;

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

    /**
     * @Author: DCY.
     * @Cretion Date: 2017-11-28.
     * @Description: Trace返回.
     */
    public static class ControllerTraceResponse {
        private boolean success;

        private List<List<String>> results;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public List<List<String>> getResults() {
            return results;
        }

        public void setResults(List<List<String>> results) {
            this.results = results;
        }
    }


    /**
     * 下发tunnel命令：开启，关闭，修改
     */
    public static class IssuedTunnelCommand {
        private List<TunnelConfig> tunnel;

        public static IssuedTunnelCommand valueOf(List<TunnelConfig> tunnelList){
            IssuedTunnelCommand issuedTunnelCommand = new IssuedTunnelCommand();
            issuedTunnelCommand.setTunnel(tunnelList);
            return issuedTunnelCommand;
        }

        public List<TunnelConfig> getTunnel() {
            return tunnel;
        }

        public void setTunnel(List<TunnelConfig> tunnel) {
            this.tunnel = tunnel;
        }
    }

    /**
     * Create by DCY on 2017/10/12
     * TUNNEL下发 tunnel 配置
     */
    public static class TunnelConfig{
        private String tunnel_id;
        private String[] cross_tunnel;
        private String[] same_switch;
        private List<TunnelMplsConfig> mpls_switches;
        private List<TunnelSdnConfig> sdn_switches;

        public String getTunnel_id() {
            return tunnel_id;
        }

        public void setTunnel_id(String tunnel_id) {
            this.tunnel_id = tunnel_id;
        }

        public List<TunnelMplsConfig> getMpls_switches() {
            return mpls_switches;
        }

        public void setMpls_switches(List<TunnelMplsConfig> mpls_switches) {
            this.mpls_switches = mpls_switches;
        }

        public List<TunnelSdnConfig> getSdn_switches() {
            return sdn_switches;
        }

        public void setSdn_switches(List<TunnelSdnConfig> sdn_switches) {
            this.sdn_switches = sdn_switches;
        }

        public String[] getCross_tunnel() {
            return cross_tunnel;
        }

        public void setCross_tunnel(String[] cross_tunnel) {
            this.cross_tunnel = cross_tunnel;
        }

        public String[] getSame_switch() {
            return same_switch;
        }

        public void setSame_switch(String[] same_switch) {
            this.same_switch = same_switch;
        }
    }

    /**
     * Create by DCY on 2017/10/12
     * TUNNEL下发 MPLS 配置
     */
    public static class TunnelMplsConfig {
        private String uuid;
        private String switch_type;
        private String sub_type;
        private Integer vni;
        private String m_ip;
        private String remote_ip;
        private String port_name;
        private Integer vlan_id;
        private String inner_vlan_id;
        private String username;
        private String password;
        private String network_type;
        private Long bandwidth;
        private String sortTag;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getSwitch_type() {
            return switch_type;
        }

        public void setSwitch_type(String switch_type) {
            this.switch_type = switch_type;
        }

        public String getSub_type() {
            return sub_type;
        }

        public void setSub_type(String sub_type) {
            this.sub_type = sub_type;
        }

        public Integer getVni() {
            return vni;
        }

        public void setVni(Integer vni) {
            this.vni = vni;
        }

        public String getRemote_ip() {
            return remote_ip;
        }

        public void setRemote_ip(String remote_ip) {
            this.remote_ip = remote_ip;
        }

        public String getPort_name() {
            return port_name;
        }

        public void setPort_name(String port_name) {
            this.port_name = port_name;
        }

        public Integer getVlan_id() {
            return vlan_id;
        }

        public void setVlan_id(Integer vlan_id) {
            this.vlan_id = vlan_id;
        }

        public String getM_ip() {
            return m_ip;
        }

        public void setM_ip(String m_ip) {
            this.m_ip = m_ip;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNetwork_type() {
            return network_type;
        }

        public void setNetwork_type(String network_type) {
            this.network_type = network_type;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public String getInner_vlan_id() {
            return inner_vlan_id;
        }

        public void setInner_vlan_id(String inner_vlan_id) {
            this.inner_vlan_id = inner_vlan_id;
        }

        public String getSortTag() {
            return sortTag;
        }

        public void setSortTag(String sortTag) {
            this.sortTag = sortTag;
        }
    }

    /**
     * Create by DCY on 2017/10/12
     * TUNNEL下发 SDN 配置
     */
    public static class TunnelSdnConfig {
        private String uuid;
        private String m_ip;
        private Integer vlan_id;
        private String inner_vlan_id;
        private String in_port;
        private String uplink;
        private String network_type;
        private Long bandwidth;
        private String sortTag;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getM_ip() {
            return m_ip;
        }

        public void setM_ip(String m_ip) {
            this.m_ip = m_ip;
        }

        public Integer getVlan_id() {
            return vlan_id;
        }

        public void setVlan_id(Integer vlan_id) {
            this.vlan_id = vlan_id;
        }

        public String getInner_vlan_id() {
            return inner_vlan_id;
        }

        public void setInner_vlan_id(String inner_vlan_id) {
            this.inner_vlan_id = inner_vlan_id;
        }

        public String getIn_port() {
            return in_port;
        }

        public void setIn_port(String in_port) {
            this.in_port = in_port;
        }

        public String getUplink() {
            return uplink;
        }

        public void setUplink(String uplink) {
            this.uplink = uplink;
        }

        public String getNetwork_type() {
            return network_type;
        }

        public void setNetwork_type(String network_type) {
            this.network_type = network_type;
        }

        public Long getBandwidth() {
            return bandwidth;
        }

        public void setBandwidth(Long bandwidth) {
            this.bandwidth = bandwidth;
        }

        public String getSortTag() {
            return sortTag;
        }

        public void setSortTag(String sortTag) {
            this.sortTag = sortTag;
        }
    }


}
