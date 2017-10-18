package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ResourceVO.class)
public class ResourceVO_ {
    public static volatile SingularAttribute<ResourceVO, String> productUuid;
    public static volatile SingularAttribute<ResourceVO, String> productName;
    public static volatile SingularAttribute<ResourceVO, ProductType> productType;
    public static volatile SingularAttribute<ResourceVO, String> description;
    public static volatile SingularAttribute<ResourceVO, String> networkSegmentA;
    public static volatile SingularAttribute<ResourceVO, String> networkSegmentB;
}
