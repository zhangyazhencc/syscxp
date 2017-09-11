package org.zstack.tunnel.header.monitor;

import org.zstack.header.vo.EO;
import org.zstack.tunnel.header.host.HostMonitorAO;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-11.
 * @Description: .
 */
@StaticMetamodel(HostSwitchMonitorAO.class)
public class HostSwitchMonitorAO_{
    public static volatile SingularAttribute<HostMonitorAO, String> uuid;
    public static volatile SingularAttribute<HostMonitorAO, String> hostUuid;
    public static volatile SingularAttribute<HostMonitorAO, String> physicalSwitchUuid;
    public static volatile SingularAttribute<HostMonitorAO, String> physicalSwitchPortName;
    public static volatile SingularAttribute<HostMonitorAO, String> interfaceName;
    public static volatile SingularAttribute<HostMonitorAO, Timestamp> createDate;
    public static volatile SingularAttribute<HostMonitorAO, Timestamp> lastOpDate;
}
