package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;
import com.syscxp.header.tunnel.tunnel.TunnelSwitchPortVO;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: 速度测试专线列表.
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class SpeedTestTunnelVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String tunnelUuid;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "uuid", insertable = false, updatable = false)
    private TunnelVO tunnelVO = new TunnelVO();

    public TunnelVO getTunnelVO() {
        return tunnelVO;
    }

    public void setTunnelVO(TunnelVO tunnelVO) {
        this.tunnelVO = tunnelVO;
    }

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
}
