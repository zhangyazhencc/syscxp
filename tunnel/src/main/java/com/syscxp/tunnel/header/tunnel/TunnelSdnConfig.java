package com.syscxp.tunnel.header.tunnel;

/**
 * Create by DCY on 2017/10/12
 */
public class TunnelSdnConfig {
    private String uuid;
    private String m_ip;
    private Integer vlan_id;
    private String inner_vlan_id;
    private String in_port;
    private String uplink;
    private String network_type;
    private Long bandwidth;

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
}
