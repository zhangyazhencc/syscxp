package com.syscxp.header.tunnel.network;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(L3EndpointVO.class)
public class L3EndpointVO_ {

    public static volatile SingularAttribute<L3EndpointVO,String> uuid;
    public static volatile SingularAttribute<L3EndpointVO,String> l3NetworkUuid;
    public static volatile SingularAttribute<L3EndpointVO,String> endpointUuid;
    public static volatile SingularAttribute<L3EndpointVO,String> bandwidthOffering;
    public static volatile SingularAttribute<L3EndpointVO,Long> bandwidth;
    public static volatile SingularAttribute<L3EndpointVO,String> routeType;
    public static volatile SingularAttribute<L3EndpointVO,L3EndpointState> state;
    public static volatile SingularAttribute<L3EndpointVO,L3EndpointStatus> status;
    public static volatile SingularAttribute<L3EndpointVO,Integer> maxRouteNum;
    public static volatile SingularAttribute<L3EndpointVO,String> localIP;
    public static volatile SingularAttribute<L3EndpointVO,String> remoteIp;
    public static volatile SingularAttribute<L3EndpointVO,String> monitorIp;
    public static volatile SingularAttribute<L3EndpointVO,String> netmask;
    public static volatile SingularAttribute<L3EndpointVO,String> ipCidr;
    public static volatile SingularAttribute<L3EndpointVO,String> interfaceUuid;
    public static volatile SingularAttribute<L3EndpointVO,String> switchPortUuid;
    public static volatile SingularAttribute<L3EndpointVO,String> physicalSwitchUuid;
    public static volatile SingularAttribute<L3EndpointVO,Integer> vlan;
    public static volatile SingularAttribute<L3EndpointVO,String> rd;
    public static volatile SingularAttribute<L3EndpointVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<L3EndpointVO,Timestamp> createDate;

}
