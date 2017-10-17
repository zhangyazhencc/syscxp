package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table
@Entity
public class RegulationVO  extends BaseVO{

    @Column
    private String comparisonRuleUuid;

    @Column
    private String monitorTargetUuid;

    @Column
    private int alarmThreshold;

    @Column
    private int detectPeriod;

    @Column
    private int triggerPeriod;

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
