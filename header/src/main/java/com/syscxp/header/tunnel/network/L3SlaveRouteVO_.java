package com.syscxp.header.tunnel.network;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/4/19
 */
@StaticMetamodel(L3SlaveRouteVO.class)
public class L3SlaveRouteVO_ {
    public static volatile SingularAttribute<L3RouteVO,String> uuid;
    public static volatile SingularAttribute<L3RouteVO,String> l3RouteUuid;
    public static volatile SingularAttribute<L3RouteVO,String> routeIp;
    public static volatile SingularAttribute<L3RouteVO,Integer> preference;
    public static volatile SingularAttribute<L3RouteVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<L3RouteVO,Timestamp> createDate;
}
