package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PolicyVO.class)
public class PolicyVO_ {

    public static volatile SingularAttribute<ResourceVO, ProductType> productType;
    public static volatile SingularAttribute<ResourceVO, String> name;
    public static volatile SingularAttribute<ResourceVO, String> description;
    public static volatile SingularAttribute<ResourceVO, Integer> bindResources;
}
