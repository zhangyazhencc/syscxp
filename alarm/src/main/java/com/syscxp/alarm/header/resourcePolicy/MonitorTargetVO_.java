package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO_;
import com.syscxp.header.billing.ProductType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel( MonitorTargetVO.class)
public class MonitorTargetVO_  extends BaseVO_{

    public static volatile SingularAttribute<MonitorTargetVO, ProductType> productType;
    public static volatile SingularAttribute<MonitorTargetVO, String> targetName;
    public static volatile SingularAttribute<MonitorTargetVO, String> targetValue;
}
