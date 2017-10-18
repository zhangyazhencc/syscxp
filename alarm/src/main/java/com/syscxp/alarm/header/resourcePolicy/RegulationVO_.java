package com.syscxp.alarm.header.resourcePolicy;


import com.syscxp.alarm.header.BaseVO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(RegulationVO.class)
public class RegulationVO_  extends BaseVO_ {

    public static volatile SingularAttribute<ResourceVO, String> comparisonRuleUuid;
    public static volatile SingularAttribute<ResourceVO, String> monitorTargetUuid;
    public static volatile SingularAttribute<ResourceVO, Integer> alarmThreshold;
    public static volatile SingularAttribute<ResourceVO, Integer> detectPeriod;
    public static volatile SingularAttribute<ResourceVO, Integer> triggerPeriod;
}
