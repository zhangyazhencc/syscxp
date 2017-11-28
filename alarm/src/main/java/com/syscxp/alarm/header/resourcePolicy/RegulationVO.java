package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO;

import javax.persistence.*;

@Table
@Entity
public class RegulationVO  extends BaseVO{

    @Column
    private String policyUuid;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="comparisonRuleUuid")
    private ComparisonRuleVO comparisonRuleVO;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="monitorTargetUuid")
    private MonitorTargetVO monitorTargetVO;

    @Column
    private float alarmThreshold;

    @Column
    private int detectPeriod;

    @Column
    private int triggerPeriod;

    public float getAlarmThreshold() {
        return alarmThreshold;
    }

    public void setAlarmThreshold(float alarmThreshold) {
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

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public ComparisonRuleVO getComparisonRuleVO() {
        return comparisonRuleVO;
    }

    public void setComparisonRuleVO(ComparisonRuleVO comparisonRuleVO) {
        this.comparisonRuleVO = comparisonRuleVO;
    }

    public MonitorTargetVO getMonitorTargetVO() {
        return monitorTargetVO;
    }

    public void setMonitorTargetVO(MonitorTargetVO monitorTargetVO) {
        this.monitorTargetVO = monitorTargetVO;
    }
}
