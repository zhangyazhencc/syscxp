package com.syscxp.alarm.header.resourcePolicy;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(RegulationVO.class)
public class RegulationVO_ {

    public static volatile SingularAttribute<ResourceVO, String> comparisonRuleUuid;
    public static volatile SingularAttribute<ResourceVO, String> monitorTargetUuid;
    public static volatile SingularAttribute<ResourceVO, Integer> alarmThreshold;
    public static volatile SingularAttribute<ResourceVO, Integer> detectPeriod;
    public static volatile SingularAttribute<ResourceVO, Integer> triggerPeriod;
}
