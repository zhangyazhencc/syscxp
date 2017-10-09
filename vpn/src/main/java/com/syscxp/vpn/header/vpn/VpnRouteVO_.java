package com.syscxp.vpn.header.vpn;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;
import java.util.List;

@StaticMetamodel(VpnRouteVO.class)
public class VpnRouteVO_ {
    public static volatile SingularAttribute<VpnRouteVO, String> uuid;
    public static volatile SingularAttribute<VpnRouteVO, String> vpnUuid;
    public static volatile SingularAttribute<VpnRouteVO, RouteType> routeType;
    public static volatile SingularAttribute<VpnRouteVO, List> nextIface;
    public static volatile SingularAttribute<VpnRouteVO, String> targetCidr;
    public static volatile SingularAttribute<VpnRouteVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnRouteVO, Timestamp> createDate;
}
