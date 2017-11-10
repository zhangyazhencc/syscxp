package com.syscxp.header.tunnel.monitor;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-25.
 * @Description: 监控通道两端信息表.
 */
@StaticMetamodel(TunnelMonitorInterfaceVO.class)
public class TunnelMonitorInterfaceVO_ {
    public static volatile SingularAttribute<TunnelMonitorVO,String> uuid;
    public static volatile SingularAttribute<TunnelMonitorVO,String> tunnelMonitorUuid;
    public static volatile SingularAttribute<TunnelMonitorVO,String> interfaceUuid;
    public static volatile SingularAttribute<TunnelMonitorVO,InterfaceType> interfaceType;
    public static volatile SingularAttribute<TunnelMonitorVO,String> hostUuid;
    public static volatile SingularAttribute<TunnelMonitorVO,String> monitorIp;
}
