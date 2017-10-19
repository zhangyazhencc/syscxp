package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PolicyRegulationRefVO.class)
public class PolicyRegulationRefVO_  extends BaseVO_ {

    public static volatile SingularAttribute<ResourceVO, String> policyUuid;
    public static volatile SingularAttribute<ResourceVO, String> regulationUuid;
}
