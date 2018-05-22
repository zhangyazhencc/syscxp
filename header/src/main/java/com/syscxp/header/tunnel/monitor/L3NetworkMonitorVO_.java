package com.syscxp.header.tunnel.monitor;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
@StaticMetamodel(L3NetworkMonitorVO.class)
public class L3NetworkMonitorVO_ {
    public static volatile SingularAttribute<L3NetworkMonitorVO,String> uuid;
    public static volatile SingularAttribute<L3NetworkMonitorVO, String> l3NetworkUuid;
    public static volatile SingularAttribute<L3NetworkMonitorVO, String> srcL3EndpointUuid;
    public static volatile SingularAttribute<L3NetworkMonitorVO, String> dstL3EndpointUuid;
}
