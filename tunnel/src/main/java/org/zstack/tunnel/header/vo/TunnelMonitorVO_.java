package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(TunnelMonitorVO.class)
public class TunnelMonitorVO_ {
    public static volatile SingularAttribute<TunnelMonitorVO, String> uuid;
    public static volatile SingularAttribute<TunnelMonitorVO, String> tunnelUuid;
    public static volatile SingularAttribute<TunnelMonitorVO, String> tunnelPointA;
    public static volatile SingularAttribute<TunnelMonitorVO, String> tunnelPointB;
    public static volatile SingularAttribute<TunnelMonitorVO, String> monitorAIp;
    public static volatile SingularAttribute<TunnelMonitorVO, String> monitorBIp;
    public static volatile SingularAttribute<TunnelMonitorVO, String> status;
    public static volatile SingularAttribute<TunnelMonitorVO, String> msg;
    public static volatile SingularAttribute<TunnelMonitorVO, Timestamp> createDate;
    public static volatile SingularAttribute<TunnelMonitorVO, Timestamp> lastOpDate;
}
