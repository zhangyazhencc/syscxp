package com.syscxp.header.vpn.host;

import com.syscxp.header.host.HostVO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(VpnHostVO.class)
public class VpnHostVO_ extends HostVO_{
    public static volatile SingularAttribute<VpnHostVO, String> zoneUuid;
    public static volatile SingularAttribute<VpnHostVO, String> publicInterface;
    public static volatile SingularAttribute<VpnHostVO, String> publicIp;
    public static volatile SingularAttribute<VpnHostVO, Integer> sshPort;
    public static volatile SingularAttribute<VpnHostVO, String> username;
    public static volatile SingularAttribute<VpnHostVO, String> password;
    public static volatile SingularAttribute<VpnHostVO, String> vpnInterfaceName;
    public static volatile SingularAttribute<VpnHostVO, Integer> startPort;
    public static volatile SingularAttribute<VpnHostVO, Integer> endPort;
}
