package com.syscxp.header.tunnel.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/11/28
 */
@StaticMetamodel(TraceRouteVO.class)
public class TraceRouteVO_ {
    public static volatile SingularAttribute<TraceRouteVO, String> uuid;
    public static volatile SingularAttribute<TraceRouteVO, String> tunnelUuid;
    public static volatile SingularAttribute<TraceRouteVO, Integer> traceSort;
    public static volatile SingularAttribute<TraceRouteVO, String> routeIP;
    public static volatile SingularAttribute<TraceRouteVO, String> timesFirst;
    public static volatile SingularAttribute<TraceRouteVO, String> timesSecond;
    public static volatile SingularAttribute<TraceRouteVO, String> timesThird;
    public static volatile SingularAttribute<TraceRouteVO, Timestamp> createDate;
    public static volatile SingularAttribute<TraceRouteVO, Timestamp> lastOpDate;
}
