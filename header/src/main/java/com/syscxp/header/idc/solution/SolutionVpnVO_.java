package com.syscxp.header.idc.solution;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by wangwg on 2017/11/20.
 */
@StaticMetamodel(SolutionVpnVO.class)
public class SolutionVpnVO_ extends SolutionBaseVO_ {

    public static volatile SingularAttribute<SolutionVpnVO, String> solutionTunnelUuid;
    public static volatile SingularAttribute<SolutionVpnVO, String> endpointUuid;
    public static volatile SingularAttribute<SolutionVpnVO, String> bandwidthOfferingUuid;

}