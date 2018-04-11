package com.syscxp.header.vpn.vpn;

import com.syscxp.header.vpn.VpnAO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(VpnVO.class)
public class VpnVO_ extends VpnAO_ {
    public static volatile SingularAttribute<VpnVO, String> endpointUuid;
    public static volatile SingularAttribute<VpnVO, String> tunnelUuid;
}
