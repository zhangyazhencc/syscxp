package com.syscxp.header.idc.solution;


import com.syscxp.header.tunnel.tunnel.TunnelType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by wangwg on 2017/11/20.
 */
@StaticMetamodel(SolutionTunnelVO.class)
public class SolutionTunnelVO_ extends SolutionBaseVO_ {

    public static volatile SingularAttribute<SolutionTunnelVO, String> endpointUuidA;
    public static volatile SingularAttribute<SolutionTunnelVO, String> endpointUuidZ;
    public static volatile SingularAttribute<SolutionTunnelVO, String> interfaceUuidA;
    public static volatile SingularAttribute<SolutionTunnelVO, String> interfaceUuidZ;
    public static volatile SingularAttribute<SolutionTunnelVO, String> bandwidthOfferingUuid;
    public static volatile SingularAttribute<SolutionTunnelVO, String> innerEndpointUuid;
    public static volatile SingularAttribute<SolutionTunnelVO, TunnelType> type;
}
