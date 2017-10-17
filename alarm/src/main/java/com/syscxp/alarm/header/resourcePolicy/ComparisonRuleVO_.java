package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel( ComparisonRuleVO.class)
public class ComparisonRuleVO_ {

    public static volatile SingularAttribute<ResourceVO, ProductType> productType;
    public static volatile SingularAttribute<ResourceVO, String> comparisonName;
}
