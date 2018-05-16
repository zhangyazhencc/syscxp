package com.syscxp.header.tunnel.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/5/9
 */
@StaticMetamodel(TETraceVO.class)
public class TETraceVO_ {
    public static volatile SingularAttribute<TETraceVO, String> uuid;
    public static volatile SingularAttribute<TETraceVO, String> teConfigUuid;
    public static volatile SingularAttribute<TETraceVO, String> switchName;
    public static volatile SingularAttribute<TETraceVO, String> switchIP;
    public static volatile SingularAttribute<TETraceVO, Integer> traceSort;

    public static volatile SingularAttribute<TETraceVO, Timestamp> createDate;
    public static volatile SingularAttribute<TETraceVO, Timestamp> lastOpDate;
}
