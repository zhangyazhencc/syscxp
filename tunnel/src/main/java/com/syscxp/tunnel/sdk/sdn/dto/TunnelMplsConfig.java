package com.syscxp.tunnel.sdk.sdn.dto;

/**
 * Create by DCY on 2017/10/12
 */
public class TunnelMplsConfig {
    private String uuid;
    private String switch_type;
    private String sub_type;
    private Integer vni;
    private String remote_ip;
    private String port_name;
    private Integer vlan_id;
    private String inner_vlan_id;
    private String m_ip;
    private String username;
    private String password;
    private String network_type;
    private Long bandwidth;

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
}
