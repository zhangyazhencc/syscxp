package com.syscxp.header.tunnel.monitor;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: .
 */
@StaticMetamodel(SpeedTestTunnelVO.class)
public class SpeedTestTunnelVO_ {
    public static volatile SingularAttribute<TunnelMonitorVO,String> uuid;
    public static volatile SingularAttribute<TunnelMonitorVO, String> tunnelUuid;
}
