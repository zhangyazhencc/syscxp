package com.syscxp.tunnel.header.monitor;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/9/26
 */
@StaticMetamodel(MonitorCidrVO.class)
public class MonitorCidrVO_ {

    public static volatile SingularAttribute<MonitorCidrVO, String> uuid;
    public static volatile SingularAttribute<MonitorCidrVO, String> monitorCidr;
    public static volatile SingularAttribute<MonitorCidrVO, String> startAddress;
    public static volatile SingularAttribute<MonitorCidrVO, String> endAddress;
    public static volatile SingularAttribute<MonitorCidrVO, Timestamp> createDate;
    public static volatile SingularAttribute<MonitorCidrVO, Timestamp> lastOpDate;
}
