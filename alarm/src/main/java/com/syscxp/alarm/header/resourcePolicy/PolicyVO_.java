package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO_;
import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PolicyVO.class)
public class PolicyVO_  extends BaseVO_ {

    public static volatile SingularAttribute<ResourceVO, ProductType> productType;
    public static volatile SingularAttribute<ResourceVO, String> name;
    public static volatile SingularAttribute<ResourceVO, String> description;
    public static volatile SingularAttribute<ResourceVO, Integer> bindResources;
}
