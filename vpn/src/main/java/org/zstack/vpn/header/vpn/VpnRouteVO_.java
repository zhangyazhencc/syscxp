package org.zstack.vpn.header.vpn;

import org.zstack.vpn.header.host.VpnHostVO;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnRouteVO.class)
public class VpnRouteVO_ {
    public static volatile SingularAttribute<VpnHostVO, String> uuid;
    public static volatile SingularAttribute<VpnHostVO, String> gatewayUuid;
    public static volatile SingularAttribute<VpnHostVO, RouteType> routeType;
    public static volatile SingularAttribute<VpnHostVO, String> nextIface;
    public static volatile SingularAttribute<VpnHostVO, String> nextIface2;
    public static volatile SingularAttribute<VpnHostVO, String> targetCidr;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> createDate;
}
