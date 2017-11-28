package com.syscxp.alarm.header.resourcePolicy;


import com.syscxp.alarm.header.BaseVO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(RegulationVO.class)
public class RegulationVO_  extends BaseVO_ {

    public static volatile SingularAttribute<RegulationVO, String> comparisonRuleUuid;
    public static volatile SingularAttribute<RegulationVO, String> policyUuid;
    public static volatile SingularAttribute<RegulationVO, String> monitorTargetUuid;
    public static volatile SingularAttribute<RegulationVO, Float> alarmThreshold;
    public static volatile SingularAttribute<RegulationVO, Integer> detectPeriod;
    public static volatile SingularAttribute<RegulationVO, Integer> triggerPeriod;
}
