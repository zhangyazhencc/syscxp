package com.syscxp.tunnel.sdk.sdn.dto;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-26.
 * @Description: RYU 控制器监控下发信息.
 */
public class MonitorMplsConfig {

    private String switch_type;
    private String sub_type;
    private String port_name;
    private Integer vlan_id;
    private String m_ip;
    private String username;
    private String password;
    private Integer bandwidth;

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

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    @Override
    public String toString() {
        return "MonitorMplsConfig{" +
                "switch_type='" + switch_type + '\'' +
                ", sub_type='" + sub_type + '\'' +
                ", port_name='" + port_name + '\'' +
                ", vlan_id=" + vlan_id +
                ", m_ip='" + m_ip + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", bandwidth=" + bandwidth +
                '}';
    }
}

