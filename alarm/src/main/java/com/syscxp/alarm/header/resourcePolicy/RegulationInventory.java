package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = RegulationVO.class)
public class RegulationInventory {

    private String uuid;

    private String policyUuid;

    private ComparisonRuleVO comparisonRuleVO;

    private MonitorTargetVO monitorTargetVO;

    private int alarmThreshold;

    private int detectPeriod;

    private int triggerPeriod;

    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static RegulationInventory valueOf(RegulationVO vo) {
        RegulationInventory inv = new RegulationInventory();
        inv.setUuid(vo.getUuid());
        inv.setAlarmThreshold(vo.getAlarmThreshold());
        inv.setDetectPeriod(vo.getDetectPeriod());
        inv.setMonitorTargetVO(vo.getMonitorTargetVO());
        inv.setComparisonRuleVO(vo.getComparisonRuleVO());
        inv.setPolicyUuid(vo.getPolicyUuid());
        inv.setTriggerPeriod(vo.getTriggerPeriod());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<RegulationInventory> valueOf(Collection<RegulationVO> vos) {
        List<RegulationInventory> lst = new ArrayList<>(vos.size());
        for (RegulationVO vo : vos) {
            lst.add(RegulationInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
