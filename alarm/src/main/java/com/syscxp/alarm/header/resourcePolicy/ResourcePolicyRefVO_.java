package com.syscxp.alarm.header.resourcePolicy;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ResourcePolicyRefVO.class)
public class ResourcePolicyRefVO_ {

    public static volatile SingularAttribute<ResourceVO, String> resourceUuid;
    public static volatile SingularAttribute<ResourceVO, String> policyUuid;
}