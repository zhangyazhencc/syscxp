package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {AlarmConstant.ACTION_SERVICE}, category = AlarmConstant.ACTION_CATEGORY_ALARM, names = {"update"})
public class APIUpdateRegulationMsg extends APIMessage{

    @APIParam(emptyString = false,resourceType = RegulationVO.class)
    private String uuid;

    @APIParam(required = false,resourceType = ComparisonRuleVO.class)
    private String comparisonRuleUuid;

    @APIParam(required = false)
    private float alarmThreshold;

    @APIParam(required = false)
    private int detectPeriod;

    @APIParam(required = false)
    private int triggerPeriod;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getComparisonRuleUuid() {
        return comparisonRuleUuid;
    }

    public void setComparisonRuleUuid(String comparisonRuleUuid) {
        this.comparisonRuleUuid = comparisonRuleUuid;
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

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update RegulationVO")
                        .resource(uuid, RegulationVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
