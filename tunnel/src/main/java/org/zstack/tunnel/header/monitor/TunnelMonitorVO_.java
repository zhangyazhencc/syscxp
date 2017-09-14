package org.zstack.tunnel.header.monitor;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-14.
 * @Description: .
 */
@StaticMetamodel(TunnelMonitorVO.class)
public class TunnelMonitorVO_ {
    public static volatile SingularAttribute<TunnelMonitorVO,String> uuid;
    public static volatile SingularAttribute<TunnelMonitorVO, String> tunnelUuid;
    public static volatile SingularAttribute<TunnelMonitorVO, String> hostAUuid;
    public static volatile SingularAttribute<TunnelMonitorVO, String> monitorAIp;
    public static volatile SingularAttribute<TunnelMonitorVO, String> hostZUuid;
    public static volatile SingularAttribute<TunnelMonitorVO, String> monitorZIp;
    public static volatile SingularAttribute<TunnelMonitorVO, TunnelMonitorStatus> status;
    public static volatile SingularAttribute<TunnelMonitorVO, String> msg;
}
