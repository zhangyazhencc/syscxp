package com.syscxp.alarm.header.log;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Inventory(mappingVOClass = AlarmLogVO.class)
public class AlarmLogInventory {
    private String uuid;
    private String productUuid;
    private ProductType productType;
    private Long duration;
    private String alarmContent;
    private AlarmStatus status;
    private String accountUuid;
    private String smsContent;
    private String mailContent;
    private String policyName;
    private String eventId;
    private Timestamp alarmTime;
    private Timestamp resumeTime;
    private int count;
//    private String regulationName;

    public static AlarmLogInventory valueOf(AlarmLogVO vo){
        AlarmLogInventory inv = new AlarmLogInventory();
        inv.setUuid(vo.getUuid());
        inv.setProductUuid(vo.getProductUuid());
        inv.setProductType(vo.getProductType());
        inv.setDuration(vo.getDuration());
        inv.setAlarmContent(vo.getAlarmContent());
        inv.setStatus(vo.getStatus());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setSmsContent(vo.getSmsContent());
        inv.setMailContent(vo.getMailContent());
        if(vo.getPolicyVO() != null){
            inv.setPolicyName(vo.getPolicyVO().getName());
        }
        inv.setEventId(vo.getEventId());
        inv.setAlarmTime(vo.getAlarmTime());
        inv.setResumeTime(vo.getResumeTime());
        inv.setCount(vo.getCount());
//        inv.setRegulationName(vo.getRegulationName());
        return inv;
    }


    public static List<AlarmLogInventory> valueOf(Collection<AlarmLogVO> vos) {
        List<AlarmLogInventory> lst = new ArrayList<>(vos.size());
        for (AlarmLogVO vo : vos) {
            lst.add(AlarmLogInventory.valueOf(vo));
        }
        return lst;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public AlarmStatus getStatus() {
        return status;
    }

    public void setStatus(AlarmStatus status) {
        this.status = status;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Timestamp getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Timestamp alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Timestamp getResumeTime() {
        return resumeTime;
    }

    public void setResumeTime(Timestamp resumeTime) {
        this.resumeTime = resumeTime;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
}
