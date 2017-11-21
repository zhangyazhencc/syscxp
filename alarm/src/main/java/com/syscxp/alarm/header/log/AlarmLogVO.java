package com.syscxp.alarm.header.log;

import com.syscxp.alarm.header.BaseVO;
import com.syscxp.alarm.header.resourcePolicy.PolicyVO;
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
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private long duration;

    @Column
    private String alarmContent;

    @Column
    @Enumerated(EnumType.STRING)
    private AlarmStatus status;

    @Column
    private String accountUuid;

    @Column
    private String smsContent;

    @Column
    private String mailContent;

    @Column
    private String regulationUuid;

//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "policyUuid")
//    private PolicyVO policyVO;

    @Column
    private String regulationName;


    @Column
    private String eventId;

    @Column
    private Timestamp alarmTime;

    @Column
    private Timestamp resumeTime;

    @Column
    private int count;

    public String getRegulationUuid() {
        return regulationUuid;
    }

    public void setRegulationUuid(String regulationUuid) {
        this.regulationUuid = regulationUuid;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getRegulationName() {
        return regulationName;
    }

    public void setRegulationName(String regulationName) {
        this.regulationName = regulationName;
    }
}
