package com.syscxp.tunnel.header.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-18
 */
@StaticMetamodel(TunnelInterfaceVO.class)
public class TunnelInterfaceVO_ {
    public static volatile SingularAttribute<TunnelInterfaceVO, String> uuid;
    public static volatile SingularAttribute<TunnelInterfaceVO, String> tunnelUuid;
    public static volatile SingularAttribute<TunnelInterfaceVO, String> interfaceUuid;
    public static volatile SingularAttribute<TunnelInterfaceVO, Integer> vlan;
    public static volatile SingularAttribute<TunnelInterfaceVO, String> sortTag;
    public static volatile SingularAttribute<TunnelInterfaceVO, TunnelQinqState> qinqState;
    public static volatile SingularAttribute<TunnelInterfaceVO, Timestamp> createDate;
    public static volatile SingularAttribute<TunnelInterfaceVO, Timestamp> lastOpDate;
}
