package org.zstack.tunnel.header.monitor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: 监控主机与交换机接口映射.
 */
@MappedSuperclass
public class HostSwitchMonitorAO {
    @Id
    @Column
    private String uuid;

    @Column
    private String hostUuid;

    @Column
    private String physicalSwitchUuid;

    @Column
    private String physicalSwitchPortName;

    @Column
    private String interfaceName;

    @Column
    private Timestamp lastOpDate;

    @Column
    private Timestamp createDate;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getPhysicalSwitchUuid() {
        return physicalSwitchUuid;
    }

    public void setPhysicalSwitchUuid(String physicalSwitchUuid) {
        this.physicalSwitchUuid = physicalSwitchUuid;
    }

    public String getPhysicalSwitchPortName() {
        return physicalSwitchPortName;
    }

    public void setPhysicalSwitchPortName(String physicalSwitchPortName) {
        this.physicalSwitchPortName = physicalSwitchPortName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
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

