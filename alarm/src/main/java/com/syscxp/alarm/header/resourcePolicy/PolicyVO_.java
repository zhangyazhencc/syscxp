package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO_;
import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PolicyVO.class)
public class PolicyVO_  extends BaseVO_ {

    public static volatile SingularAttribute<PolicyVO, ProductType> productType;
    public static volatile SingularAttribute<PolicyVO, String> name;
    public static volatile SingularAttribute<PolicyVO, String> description;
    public static volatile SingularAttribute<PolicyVO, String> accountUuid;
    public static volatile SingularAttribute<PolicyVO, Long> bindResources;
}
