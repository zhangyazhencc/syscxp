package com.syscxp.tunnel.solution.header;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by wangwg on 2017/11/20.
 */
@StaticMetamodel(SolutionTunnelVO.class)
public class SolutionTunnelVO_ extends SolutionBaseVO_ {

    public static volatile SingularAttribute<SolutionTunnelVO, String> endpointNameA;
    public static volatile SingularAttribute<SolutionTunnelVO, String> endpointNameZ;
    public static volatile SingularAttribute<SolutionTunnelVO, Long> bandwidth;

}
