package com.syscxp.header.tunnel.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/5/9
 */
@StaticMetamodel(TEConfigVO.class)
public class TEConfigVO_ {
    public static volatile SingularAttribute<TEConfigVO, String> uuid;
    public static volatile SingularAttribute<TEConfigVO, String> tunnelUuid;
    public static volatile SingularAttribute<TEConfigVO, TETraceType> traceType;

    public static volatile SingularAttribute<TEConfigVO, String> source;
    public static volatile SingularAttribute<TEConfigVO, String> target;
    public static volatile SingularAttribute<TEConfigVO, String> inNodes;
    public static volatile SingularAttribute<TEConfigVO, String> exNodes;
    public static volatile SingularAttribute<TEConfigVO, String> blurryInNodes;
    public static volatile SingularAttribute<TEConfigVO, String> blurryExNodes;
    public static volatile SingularAttribute<TEConfigVO, String> connInNodes;
    public static volatile SingularAttribute<TEConfigVO, String> connExNodes;
    public static volatile SingularAttribute<TEConfigVO, String> command;
    public static volatile SingularAttribute<TEConfigVO, TETraceStatus> status;
    public static volatile SingularAttribute<TEConfigVO, Integer> isConnected;

    public static volatile SingularAttribute<TEConfigVO, Timestamp> createDate;
    public static volatile SingularAttribute<TEConfigVO, Timestamp> lastOpDate;
}
