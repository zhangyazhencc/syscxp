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
    private String productName;
    private ProductType productType;
    private Timestamp alarmTime;
    private Integer duration;
    private TimeUnit durationTimeUnit;
    private String alarmContent;
    private Timestamp resumeTime;
    private String status;
    private String accountUuid;



    public static AlarmLogInventory valueOf(AlarmLogVO vo){
        AlarmLogInventory inv = new AlarmLogInventory();
        inv.setUuid(vo.getUuid());
        inv.setProductUuid(vo.getProductUuid());
        inv.setProductName(vo.getProductName());
        inv.setProductType(vo.getProductType());
        inv.setAlarmTime(vo.getAlarmTime());
        inv.setDuration(vo.getDuration());
        inv.setDurationTimeUnit(vo.getDurationTimeUnit());
        inv.setAlarmContent(vo.getAlarmContent());
        inv.setResumeTime(vo.getResumeTime());
        inv.setStatus(vo.getStatus());
        inv.setAccountUuid(vo.getAccountUuid());
        return inv;
    }


    public static List<AlarmLogInventory> valueOf(Collection<AlarmLogVO> vos) {
        List<AlarmLogInventory> lst = new ArrayList<>(vos.size());
        for (AlarmLogVO vo : vos) {
            lst.add(AlarmLogInventory.valueOf(vo));
        }
        return lst;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Timestamp getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Timestamp alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public TimeUnit getDurationTimeUnit() {
        return durationTimeUnit;
    }

    public void setDurationTimeUnit(TimeUnit durationTimeUnit) {
        this.durationTimeUnit = durationTimeUnit;
    }

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public Timestamp getResumeTime() {
        return resumeTime;
    }

    public void setResumeTime(Timestamp resumeTime) {
        this.resumeTime = resumeTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
