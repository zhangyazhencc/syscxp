package com.syscxp.tunnel.header.monitor;


import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-14.
 * @Description: 通道监控.
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class TunnelMonitorVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String tunnelUuid;

    @Column
    private String accountUuid;

    @Column
    private String monitorCidr;

    @Column
    private String msg;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    //private List<TunnelMonitorInterfaceVO> tunnelMonitorInterfaceVOList;

    @OneToMany
    @JoinColumn(name = "tunnelMonitorUuid")
    private List<TunnelMonitorInterfaceVO> tunnelMonitorInterfaceVOList = new ArrayList<TunnelMonitorInterfaceVO>();

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public List<TunnelMonitorInterfaceVO> getTunnelMonitorInterfaceVOList() {
        return tunnelMonitorInterfaceVOList;
    }

    public void setTunnelMonitorInterfaceVOList(List<TunnelMonitorInterfaceVO> tunnelMonitorInterfaceVOList) {
        this.tunnelMonitorInterfaceVOList = tunnelMonitorInterfaceVOList;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getMonitorCidr() {
        return monitorCidr;
    }

    public void setMonitorCidr(String monitorCidr) {
        this.monitorCidr = monitorCidr;
    }
}
