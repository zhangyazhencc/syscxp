package org.zstack.tunnel.header.monitor;

import org.zstack.header.search.Inventory;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-26.
 * @Description: RYU 控制器监控下发.
 */
public class
TunnelMonitorIssueDetailInventory {

    private Integer vlan_id;
    private String m_ip;
    private String in_port;
    private String nw_src;
    private String nw_dst;
    private String out_port;
    private Integer bandwidth;

   /* public static TunnelMonitorIssueDetailInventory valueOf(TunnelMonitorVO vo){

        return null;
    }

    public static List<TunnelMonitorIssueDetailInventory> valueOf(List<TunnelMonitorVO> vos){

        return null;
    }*/

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

    public String getOut_port() {
        return out_port;
    }

    public void setOut_port(String out_port) {
        this.out_port = out_port;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }
}

