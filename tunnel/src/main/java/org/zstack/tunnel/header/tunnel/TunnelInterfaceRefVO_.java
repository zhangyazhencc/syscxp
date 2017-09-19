package org.zstack.tunnel.header.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-18
 */
@StaticMetamodel(TunnelInterfaceRefVO.class)
public class TunnelInterfaceRefVO_ {
    public static volatile SingularAttribute<TunnelInterfaceRefVO, String> uuid;
    public static volatile SingularAttribute<TunnelInterfaceRefVO, String> tunnelUuid;
    public static volatile SingularAttribute<TunnelInterfaceRefVO, String> interfaceUuid;
    public static volatile SingularAttribute<TunnelInterfaceRefVO, Integer> innerVlan;
    public static volatile SingularAttribute<TunnelInterfaceRefVO, TunnelQinqState> qinqState;
    public static volatile SingularAttribute<TunnelInterfaceRefVO, Timestamp> createDate;
    public static volatile SingularAttribute<TunnelInterfaceRefVO, Timestamp> lastOpDate;
}
