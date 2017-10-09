package com.syscxp.vpn.header.vpn;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(VpnInterfaceVO.class)
public class VpnInterfaceVO_ {
    public static volatile SingularAttribute<VpnInterfaceVO, String> uuid;
    public static volatile SingularAttribute<VpnInterfaceVO, String> vpnUuid;
    public static volatile SingularAttribute<VpnInterfaceVO, String> name;
    public static volatile SingularAttribute<VpnInterfaceVO, String> description;
    public static volatile SingularAttribute<VpnInterfaceVO, String> tunnel;
    public static volatile SingularAttribute<VpnInterfaceVO, String> localIp;
    public static volatile SingularAttribute<VpnInterfaceVO, String> vlan;
    public static volatile SingularAttribute<VpnInterfaceVO, String> mask;
    public static volatile SingularAttribute<VpnInterfaceVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<VpnInterfaceVO, Timestamp> createDate;
}
