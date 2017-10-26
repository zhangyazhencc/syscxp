package com.syscxp.alarm.header.log;

import com.syscxp.alarm.header.BaseVO;
import com.syscxp.header.billing.ProductType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@Entity
@Table
public class AlarmLogVO extends BaseVO {

    @Column
    private String productUuid;

    @Column
    private String productName;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private Timestamp alarmTime;

    @Column
    private int duration;

    @Column
    @Enumerated(EnumType.STRING)
    private TimeUnit durationTimeUnit;

    @Column
    private String alarmContent;

    @Column
    private Timestamp resumeTime;

    @Column
    @Enumerated(EnumType.STRING)
    private AlarmStatus status;

    @Column
    private String accountUuid;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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

    public AlarmStatus getStatus() {
        return status;
    }

    public void setStatus(AlarmStatus status) {
        this.status = status;
    }
}
