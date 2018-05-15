package com.syscxp.header.tunnel.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/5/14
 */
@StaticMetamodel(VsiCurrentTraceVO.class)
public class VsiCurrentTraceVO_ {

    public static volatile SingularAttribute<VsiCurrentTraceVO, String> uuid;
    public static volatile SingularAttribute<VsiCurrentTraceVO, String> name;
    public static volatile SingularAttribute<VsiCurrentTraceVO, String> tunnelUuid;
    public static volatile SingularAttribute<VsiCurrentTraceVO, Integer> traceSort;
    public static volatile SingularAttribute<VsiCurrentTraceVO, String> switchName;
    public static volatile SingularAttribute<VsiCurrentTraceVO, String> switchIP;
    public static volatile SingularAttribute<VsiCurrentTraceVO, String> source;
    public static volatile SingularAttribute<VsiCurrentTraceVO, String> destination;
    public static volatile SingularAttribute<VsiCurrentTraceVO, String> direction;
    public static volatile SingularAttribute<VsiCurrentTraceVO, Timestamp> createDate;
    public static volatile SingularAttribute<VsiCurrentTraceVO, Timestamp> lastOpDate;
}
