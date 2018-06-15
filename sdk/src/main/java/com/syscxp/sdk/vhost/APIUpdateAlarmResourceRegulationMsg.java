package com.syscxp.sdk.vhost;


import com.syscxp.header.message.APISyncCallMessage;

public class APIUpdateAlarmResourceRegulationMsg extends APISyncCallMessage {

    private String uuid;
    private String targetValue;
    private String targetUnit;
    private String comparisonValue;
    private float alarmThreshold;
    private int detectPeriod;
    private int triggerPeriod;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public String getTargetUnit() {
        return targetUnit;
    }

    public void setTargetUnit(String targetUnit) {
        this.targetUnit = targetUnit;
    }

    public String getComparisonValue() {
        return comparisonValue;
    }

    public void setComparisonValue(String comparisonValue) {
        this.comparisonValue = comparisonValue;
    }

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
}
