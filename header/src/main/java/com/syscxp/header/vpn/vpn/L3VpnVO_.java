package com.syscxp.header.vpn.vpn;

import com.syscxp.header.vpn.VpnAO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(L3VpnVO.class)
public class L3VpnVO_ extends VpnVO_ {
    public static volatile SingularAttribute<L3VpnVO, String> l3EndpointUuid;
    public static volatile SingularAttribute<L3VpnVO, String> workMode;
    public static volatile SingularAttribute<L3VpnVO, String> startIp;
    public static volatile SingularAttribute<L3VpnVO, String> endIp;
    public static volatile SingularAttribute<L3VpnVO, String> netmask;
    public static volatile SingularAttribute<L3VpnVO, String> gateway;
    public static volatile SingularAttribute<L3VpnVO, String> remoteIp;
    public static volatile SingularAttribute<L3VpnVO, String> monitorIp;
}
