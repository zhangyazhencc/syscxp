package com.syscxp.tunnel.header.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-11-01
 */
@StaticMetamodel(TunnelSwitchVO.class)
public class TunnelSwitchVO_ {
    public static volatile SingularAttribute<TunnelSwitchVO, String> uuid;
    public static volatile SingularAttribute<TunnelSwitchVO, String> tunnelUuid;
    public static volatile SingularAttribute<TunnelSwitchVO, String> endpointUuid;
    public static volatile SingularAttribute<TunnelSwitchVO, String> switchPortUuid;
    public static volatile SingularAttribute<TunnelSwitchVO, NetworkType> type;
    public static volatile SingularAttribute<TunnelSwitchVO, Integer> vlan;
    public static volatile SingularAttribute<TunnelSwitchVO, String> sortTag;
    public static volatile SingularAttribute<TunnelSwitchVO, Timestamp> createDate;
    public static volatile SingularAttribute<TunnelSwitchVO, Timestamp> lastOpDate;
}
