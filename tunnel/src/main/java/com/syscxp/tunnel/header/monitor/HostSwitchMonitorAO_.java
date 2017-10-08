package com.syscxp.tunnel.header.monitor;

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
    public static volatile SingularAttribute<HostSwitchMonitorAO, String> uuid;
    public static volatile SingularAttribute<HostSwitchMonitorAO, String> hostUuid;
    public static volatile SingularAttribute<HostSwitchMonitorAO, String> physicalSwitchUuid;
    public static volatile SingularAttribute<HostSwitchMonitorAO, String> physicalSwitchPortName;
    public static volatile SingularAttribute<HostSwitchMonitorAO, String> interfaceName;
    public static volatile SingularAttribute<HostSwitchMonitorAO, Timestamp> createDate;
    public static volatile SingularAttribute<HostSwitchMonitorAO, Timestamp> lastOpDate;
}
