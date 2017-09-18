package org.zstack.vpn.header.gateway;

import org.zstack.vpn.header.host.VpnHostVO;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnInterfaceVO.class)
public class VpnInterfaceVO_ {
    public static volatile SingularAttribute<VpnHostVO, String> uuid;
    public static volatile SingularAttribute<VpnHostVO, String> gatewayUuid;
    public static volatile SingularAttribute<VpnHostVO, String> name;
    public static volatile SingularAttribute<VpnHostVO, String> description;
    public static volatile SingularAttribute<VpnHostVO, String> tunnel;
    public static volatile SingularAttribute<VpnHostVO, String> serverIP;
    public static volatile SingularAttribute<VpnHostVO, String> clientIP;
    public static volatile SingularAttribute<VpnHostVO, String> mask;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnHostVO, Timestamp> createDate;
}
