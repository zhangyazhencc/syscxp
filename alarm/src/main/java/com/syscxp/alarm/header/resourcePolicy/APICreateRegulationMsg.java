package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.contact.APICreateContactEvent;
import com.syscxp.alarm.header.contact.ContactVO;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {AlarmConstant.ACTION_SERVICE}, category = AlarmConstant.ACTION_CATEGORY_ALARM, names = {"create"})
public class APICreateRegulationMsg extends APIMessage{

    @APIParam(emptyString =false, resourceType = PolicyVO.class)
    private String policyUuid;

    @APIParam(emptyString =false, resourceType = ComparisonRuleVO.class)
    private String comparisonRuleUuid;

    @APIParam(emptyString =false, resourceType = MonitorTargetVO.class)
    private String monitorTargetUuid;

    @APIParam
    private float alarmThreshold;

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
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateRegulationEvent) evt).getInventory().getUuid();
                }

                ntfy("Create RegulationVO")
                        .resource(uuid, RegulationVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
