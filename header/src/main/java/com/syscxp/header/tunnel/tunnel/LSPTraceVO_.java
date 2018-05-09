package com.syscxp.header.tunnel.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/11/28
 */
@StaticMetamodel(LSPTraceVO.class)
public class LSPTraceVO_ {
    public static volatile SingularAttribute<LSPTraceVO, String> uuid;
    public static volatile SingularAttribute<LSPTraceVO, String> tunnelUuid;
    public static volatile SingularAttribute<LSPTraceVO, Integer> traceSort;
    public static volatile SingularAttribute<LSPTraceVO, String> switchName;
    public static volatile SingularAttribute<LSPTraceVO, String> routeIP;
    public static volatile SingularAttribute<LSPTraceVO, String> timesFirst;
    public static volatile SingularAttribute<LSPTraceVO, String> timesSecond;
    public static volatile SingularAttribute<LSPTraceVO, String> timesThird;
    public static volatile SingularAttribute<LSPTraceVO, Timestamp> createDate;
    public static volatile SingularAttribute<LSPTraceVO, Timestamp> lastOpDate;
}
