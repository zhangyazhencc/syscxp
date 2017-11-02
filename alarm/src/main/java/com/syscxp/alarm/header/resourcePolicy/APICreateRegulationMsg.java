package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = AlarmConstant.ACTION_CATEGORY_RESOURCE_POLICY)
public class APICreateRegulationMsg extends APIMessage{

    @APIParam(emptyString =false, resourceType = PolicyVO.class)
    private String policyUuid;

    @APIParam(emptyString =false, resourceType = ComparisonRuleVO.class)
    private String comparisonRuleUuid;

    @APIParam(emptyString =false, resourceType = MonitorTargetVO.class)
    private String monitorTargetUuid;

    @APIParam(numberRange={1,Integer.MAX_VALUE})
    private int alarmThreshold;

    @APIParam(numberRange={1,Integer.MAX_VALUE})
    private int detectPeriod;

    @APIParam(numberRange={1,Integer.MAX_VALUE})
    private int triggerPeriod;

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public String getComparisonRuleUuid() {
        return comparisonRuleUuid;
    }

    public void setComparisonRuleUuid(String comparisonRuleUuid) {
        this.comparisonRuleUuid = comparisonRuleUuid;
    }

    public String getMonitorTargetUuid() {
        return monitorTargetUuid;
    }

    public void setMonitorTargetUuid(String monitorTargetUuid) {
        this.monitorTargetUuid = monitorTargetUuid;
    }

    public int getAlarmThreshold() {
        return alarmThreshold;
    }

    public void setAlarmThreshold(int alarmThreshold) {
        this.alarmThreshold = alarmThreshold;
    }

    public int getDetectPeriod() {
        return detectPeriod;
    }

    public void setDetectPeriod(int detectPeriod) {
        this.detectPeriod = detectPeriod;
    }

    public int getTriggerPeriod() {
        return triggerPeriod;
    }

    public void setTriggerPeriod(int triggerPeriod) {
        this.triggerPeriod = triggerPeriod;
    }
}
