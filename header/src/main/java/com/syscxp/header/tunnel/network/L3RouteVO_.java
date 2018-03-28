package com.syscxp.header.tunnel.network;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(L3RouteVO.class)
public class L3RouteVO_ {
    public static volatile SingularAttribute<L3RouteVO,String> uuid;
    public static volatile SingularAttribute<L3RouteVO,String> l3EndpointUuid;
    public static volatile SingularAttribute<L3RouteVO,String> cidr;
    public static volatile SingularAttribute<L3RouteVO,String> truthCidr;
    public static volatile SingularAttribute<L3RouteVO,String> nextIp;
    public static volatile SingularAttribute<L3RouteVO,Integer> indexNum;
    public static volatile SingularAttribute<L3RouteVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<L3RouteVO,Timestamp> createDate;


}
