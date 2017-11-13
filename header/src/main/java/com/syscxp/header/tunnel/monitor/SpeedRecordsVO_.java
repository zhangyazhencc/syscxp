package com.syscxp.header.tunnel.monitor;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-18.
 * @Description: .
 */
@StaticMetamodel(SpeedRecordsVO.class)
public class SpeedRecordsVO_ {
    public static volatile SingularAttribute<TunnelMonitorVO,String> uuid;
    public static volatile SingularAttribute<TunnelMonitorVO, String> tunnelUuid;
    public static volatile SingularAttribute<TunnelMonitorVO, String> srcTunnelMonitorUuid;
    public static volatile SingularAttribute<TunnelMonitorVO, String> dstunnelMonitorUuid;
    public static volatile SingularAttribute<TunnelMonitorVO, ProtocolType> protocolType;
    public static volatile SingularAttribute<TunnelMonitorVO, SpeedRecordStatus> status;
}
