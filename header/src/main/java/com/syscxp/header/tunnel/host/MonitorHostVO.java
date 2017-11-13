package com.syscxp.header.tunnel.host;

import com.syscxp.header.core.encrypt.DECRYPT;
import com.syscxp.header.core.encrypt.ENCRYPTParam;
import com.syscxp.header.host.HostEO;
import com.syscxp.header.vo.EO;
import com.syscxp.header.host.HostVO;
import com.syscxp.header.vo.NoView;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.vo.ForeignKey;
import com.syscxp.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;

@Entity
@Table
@PrimaryKeyJoinColumn(name="uuid", referencedColumnName="uuid")
@EO(EOClazz = HostEO.class, needView = false)
public class MonitorHostVO extends HostVO {
    @Column
    private String username;

    @Column
    private MonitorType monitorType;

    @Column
    @ENCRYPTParam
    private String password;

    @Column
    private Integer sshPort;

    @Column
    @ForeignKey(parentEntityClass = NodeVO.class, onDeleteAction = ReferenceOption.RESTRICT)
    private String nodeUuid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nodeUuid", insertable = false, updatable = false)
    @NoView
    private NodeVO node;

    public NodeVO getNode() {
        return node;
    }

    public void setNode(NodeVO node) {
        this.node = node;
    }

    public MonitorHostVO() {
    }

    public MonitorHostVO(HostVO vo) {
        super(vo);
    }

    public MonitorType getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(MonitorType monitorType) {
        this.monitorType = monitorType;
    }

    @DECRYPT
    public String getPassword() {
        return password;
    }

//    @ENCRYPT
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }
}

