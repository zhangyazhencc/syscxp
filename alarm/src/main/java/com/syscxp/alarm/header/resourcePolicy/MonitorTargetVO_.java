package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel( MonitorTargetVO.class)
public class MonitorTargetVO_ {

    public static volatile SingularAttribute<ResourceVO, ProductType> productType;
    public static volatile SingularAttribute<ResourceVO, String> targetName;
}
