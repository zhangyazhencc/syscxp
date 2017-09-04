package org.zstack.tunnel.header.host;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-30
 */
@StaticMetamodel(HostMonitorAO.class)
public class HostMonitorAO_ {
    public static volatile SingularAttribute<HostMonitorAO, String> uuid;
    public static volatile SingularAttribute<HostMonitorAO, String> hostUuid;
    public static volatile SingularAttribute<HostMonitorAO, String> switchPortUuid;
    public static volatile SingularAttribute<HostMonitorAO, String> interfaceName;
    public static volatile SingularAttribute<HostMonitorAO, Timestamp> createDate;
    public static volatile SingularAttribute<HostMonitorAO, Timestamp> lastOpDate;
}
