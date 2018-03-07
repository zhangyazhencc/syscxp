package com.syscxp.tunnel.sdnController;

import java.util.List;

public class L3NetworkCommand {

    private String net_id;
    private Long vrf_id;
    private String username;
    private List<Mpls_switche> mpls_switches;

    public class Mpls_switche{
        private String uuid;
        private String switch_type;
        private String sub_type;
        private String port_name;
        private Long vlan_id;
        private String m_ip;
        private String connect_ip_local;
        private String connect_ip_remote;
        private String netmask;
        private String username;
        private String password;
        private Long bandwidth;
        private String protocol;
        private String port;
        private List<Route> routes;

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

        public String getPort_name() {
            return port_name;
        }

        public void setPort_name(String port_name) {
            this.port_name = port_name;
        }

        public Long getVlan_id() {
            return vlan_id;
        }

        public void setVlan_id(Long vlan_id) {
            this.vlan_id = vlan_id;
        }

        public String getM_ip() {
            return m_ip;
        }

        public void setM_ip(String m_ip) {
            this.m_ip = m_ip;
        }

        public String getConnect_ip_local() {
            return connect_ip_local;
        }

        public void setConnect_ip_local(String connect_ip_local) {
            this.connect_ip_local = connect_ip_local;
        }

        public String getConnect_ip_remote() {
            return connect_ip_remote;
        }

        public void setConnect_ip_remote(String connect_ip_remote) {
            this.connect_ip_remote = connect_ip_remote;
        }

        public String getNetmask() {
            return netmask;
        }

        public void setNetmask(String netmask) {
            this.netmask = netmask;
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

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public List<Route> getRoutes() {
            return routes;
        }

        public void setRoutes(List<Route> routes) {
            this.routes = routes;
        }
    }

    public class Route{
        private String business_ip;
        private String netmask;
        private String route_ip;
        private Long index;

        public String getBusiness_ip() {
            return business_ip;
        }

        public void setBusiness_ip(String business_ip) {
            this.business_ip = business_ip;
        }

        public String getNetmask() {
            return netmask;
        }

        public void setNetmask(String netmask) {
            this.netmask = netmask;
        }

        public String getRoute_ip() {
            return route_ip;
        }

        public void setRoute_ip(String route_ip) {
            this.route_ip = route_ip;
        }

        public Long getIndex() {
            return index;
        }

        public void setIndex(Long index) {
            this.index = index;
        }
    }

    public String getNet_id() {
        return net_id;
    }

    public void setNet_id(String net_id) {
        this.net_id = net_id;
    }

    public Long getVrf_id() {
        return vrf_id;
    }


    public void setVrf_id(Long vrf_id) {
        this.vrf_id = vrf_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Mpls_switche> getMpls_switches() {
        return mpls_switches;
    }

    public void setMpls_switches(List<Mpls_switche> mpls_switches) {
        this.mpls_switches = mpls_switches;
    }

}
