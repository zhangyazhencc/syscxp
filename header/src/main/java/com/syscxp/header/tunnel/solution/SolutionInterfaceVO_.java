package com.syscxp.header.tunnel.solution;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by wangwg on 2017/11/20.
 */
@StaticMetamodel(SolutionInterfaceVO.class)
public class SolutionInterfaceVO_ extends SolutionBaseVO_ {

    public static volatile SingularAttribute<SolutionInterfaceVO, String> endpointUuid;
    public static volatile SingularAttribute<SolutionInterfaceVO, String> portOfferingUuid;

}
