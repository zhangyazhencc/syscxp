package com.syscxp.header.tunnel.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-11-01
 */
@StaticMetamodel(TunnelSwitchPortVO.class)
public class TunnelSwitchPortVO_ {
    public static volatile SingularAttribute<TunnelSwitchPortVO, String> uuid;
    public static volatile SingularAttribute<TunnelSwitchPortVO, String> tunnelUuid;
    public static volatile SingularAttribute<TunnelSwitchPortVO, String> interfaceUuid;
    public static volatile SingularAttribute<TunnelSwitchPortVO, String> endpointUuid;
    public static volatile SingularAttribute<TunnelSwitchPortVO, String> switchPortUuid;
    public static volatile SingularAttribute<TunnelSwitchPortVO, NetworkType> type;
    public static volatile SingularAttribute<TunnelSwitchPortVO, Integer> vlan;
    public static volatile SingularAttribute<TunnelSwitchPortVO, String> sortTag;
    public static volatile SingularAttribute<TunnelSwitchPortVO, String> physicalSwitchUuid;
    public static volatile SingularAttribute<TunnelSwitchPortVO, String> ownerMplsSwitchUuid;
    public static volatile SingularAttribute<TunnelSwitchPortVO, String> peerMplsSwitchUuid;
    public static volatile SingularAttribute<TunnelSwitchPortVO, Timestamp> createDate;
    public static volatile SingularAttribute<TunnelSwitchPortVO, Timestamp> lastOpDate;
}
