package org.zstack.tunnel.header.monitor;

import javax.persistence.*;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-25.
 * @Description: 监控通道两端信息表.
 */
@StaticMetamodel(TunnelMonitorInterfaceVO.class)
public class TunnelMonitorInterfaceVO_ {
    public static volatile SingularAttribute<TunnelMonitorVO,String> uuid;
    public static volatile SingularAttribute<TunnelMonitorVO,String> tunnelMonitorUuid;
    public static volatile SingularAttribute<TunnelMonitorVO,InterfaceType> interfaceType;
    public static volatile SingularAttribute<TunnelMonitorVO,String> hostUuid;
    public static volatile SingularAttribute<TunnelMonitorVO,String> monitorIp;
}
