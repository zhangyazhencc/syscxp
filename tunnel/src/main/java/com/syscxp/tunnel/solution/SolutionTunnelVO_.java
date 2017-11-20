package com.syscxp.tunnel.solution;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by wangwg on 2017/11/20.
 */
@StaticMetamodel(SolutionInterfaceVO.class)
public class SolutionTunnelVO_ extends SolutionBaseVO_ {

    public static volatile SingularAttribute<SolutionInterfaceVO, String> endpointNameA;
    public static volatile SingularAttribute<SolutionInterfaceVO, String> endpointNameZ;
    public static volatile SingularAttribute<SolutionInterfaceVO, Long> bandwidth;

}
