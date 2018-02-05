package com.syscxp.header.tunnel.network;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(L3EndPointVO.class)
public class L3EndPointVO_ {

    public static volatile SingularAttribute<L3EndPointVO,String> uuid;
    public static volatile SingularAttribute<L3EndPointVO,String> l3NetworkUuid;
    public static volatile SingularAttribute<L3EndPointVO,String> endpointUuid;
    public static volatile SingularAttribute<L3EndPointVO,Long> bandwidth;
    public static volatile SingularAttribute<L3EndPointVO,String> routeType;
    public static volatile SingularAttribute<L3EndPointVO,String> status;
    public static volatile SingularAttribute<L3EndPointVO,Long> maxRouteNum;
    public static volatile SingularAttribute<L3EndPointVO,String> localIP;
    public static volatile SingularAttribute<L3EndPointVO,String> remoteIp;
    public static volatile SingularAttribute<L3EndPointVO,String> netmask;
    public static volatile SingularAttribute<L3EndPointVO,String> interfaceUuid;
    public static volatile SingularAttribute<L3EndPointVO,String> switchPortUuid;
    public static volatile SingularAttribute<L3EndPointVO,Long> vlan;
    public static volatile SingularAttribute<L3EndPointVO,String> rd;
    public static volatile SingularAttribute<L3EndPointVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<L3EndPointVO,Timestamp> createDate;

}
