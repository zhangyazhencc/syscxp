package org.zstack.tunnel.header.host;

import org.zstack.header.vo.ForeignKey;
import org.zstack.tunnel.header.switchs.SwitchPortVO;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-30
 */
@MappedSuperclass
public class HostSwitchMonitorAO {

    @Id
    @Column
    private String uuid;

    @Column
    @ForeignKey(parentEntityClass = HostEO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String hostUuid;

    @Column
    @ForeignKey(parentEntityClass = SwitchPortVO.class, onDeleteAction = ForeignKey.ReferenceOption.SET_NULL)
    private String switchPortUuid;

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

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
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
