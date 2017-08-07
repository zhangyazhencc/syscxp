package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(HostSwitchMonitorVO.class)
public class HostSwitchMonitorVO_ {
    public static volatile SingularAttribute<HostSwitchMonitorVO, String> uuid;
    public static volatile SingularAttribute<HostSwitchMonitorVO, String> switchUuid;
    public static volatile SingularAttribute<HostSwitchMonitorVO, String> hostUuid;
    public static volatile SingularAttribute<HostSwitchMonitorVO, String> interfaceName;
    public static volatile SingularAttribute<HostSwitchMonitorVO, Integer> deleted;
    public static volatile SingularAttribute<HostSwitchMonitorVO, Timestamp> createDate;
    public static volatile SingularAttribute<HostSwitchMonitorVO, Timestamp> lastOpDate;
}
