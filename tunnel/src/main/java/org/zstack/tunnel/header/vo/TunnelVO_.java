package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(TunnelVO.class)
public class TunnelVO_ {
    public static volatile SingularAttribute<TunnelVO, String> uuid;
    public static volatile SingularAttribute<TunnelVO, String> networkTypetUuid;
    public static volatile SingularAttribute<TunnelVO, String> endpointA;
    public static volatile SingularAttribute<TunnelVO, String> endpointB;
    public static volatile SingularAttribute<TunnelVO, String> endpointAType;
    public static volatile SingularAttribute<TunnelVO, String> endpointBType;
    public static volatile SingularAttribute<TunnelVO, String> projectUuid;
    public static volatile SingularAttribute<TunnelVO, String> code;
    public static volatile SingularAttribute<TunnelVO, String> name;
    public static volatile SingularAttribute<TunnelVO, Double> distance;
    public static volatile SingularAttribute<TunnelVO, Integer> bandwidth;
    public static volatile SingularAttribute<TunnelVO, Integer> vni;
    public static volatile SingularAttribute<TunnelVO, String> state;
    public static volatile SingularAttribute<TunnelVO, String> status;
    public static volatile SingularAttribute<TunnelVO, String> priExclusive;
    public static volatile SingularAttribute<TunnelVO, Integer> alarmed;
    public static volatile SingularAttribute<TunnelVO, Integer> deleted;
    public static volatile SingularAttribute<TunnelVO, Timestamp> billingDate;
    public static volatile SingularAttribute<TunnelVO, Timestamp> createDate;
    public static volatile SingularAttribute<TunnelVO, Timestamp> lastOpDate;
}
