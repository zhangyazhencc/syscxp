package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(TunnelPointVO.class)
public class TunnelPointVO_ {
    public static volatile SingularAttribute<TunnelPointVO, String> uuid;
    public static volatile SingularAttribute<TunnelPointVO, String> tunnelUuid;
    public static volatile SingularAttribute<TunnelPointVO, String> agentUuid;
    public static volatile SingularAttribute<TunnelPointVO, String> switchUuid;
    public static volatile SingularAttribute<TunnelPointVO, String> hostingUuid;
    public static volatile SingularAttribute<TunnelPointVO, String> bridgeName;
    public static volatile SingularAttribute<TunnelPointVO, Integer> meter;
    public static volatile SingularAttribute<TunnelPointVO, Integer> priority;
    public static volatile SingularAttribute<TunnelPointVO, String> role;
    public static volatile SingularAttribute<TunnelPointVO, String> hostingType;
    public static volatile SingularAttribute<TunnelPointVO, Integer> isAttached;
    public static volatile SingularAttribute<TunnelPointVO, Integer> deleted;
    public static volatile SingularAttribute<TunnelPointVO, Timestamp> createDate;
    public static volatile SingularAttribute<TunnelPointVO, Timestamp> lastOpDate;
}
