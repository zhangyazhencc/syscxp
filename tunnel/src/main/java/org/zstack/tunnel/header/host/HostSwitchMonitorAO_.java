package org.zstack.tunnel.header.host;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-30
 */
@StaticMetamodel(HostSwitchMonitorAO.class)
public class HostSwitchMonitorAO_ {
    public static volatile SingularAttribute<HostSwitchMonitorVO, String> uuid;
    public static volatile SingularAttribute<HostSwitchMonitorVO, String> hostUuid;
    public static volatile SingularAttribute<HostSwitchMonitorVO, String> switchUuid;
    public static volatile SingularAttribute<HostSwitchMonitorVO, String> interfaceName;
    public static volatile SingularAttribute<HostSwitchMonitorVO, Timestamp> createDate;
    public static volatile SingularAttribute<HostSwitchMonitorVO, Timestamp> lastOpDate;
}
