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
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private long duration;

    @Column
    private String alarmContent;

    @Column
    private String status;

    @Column
    private String accountUuid;

    @Column
    private String smsContent;

    @Column
    private String mailContent;

    @Column
    private String regulationUuid;

    @Column
    private String policyName;

    @Column
    private String eventId;

    @Column
    private Timestamp alarmTime;

    @Column
    private Timestamp resumeTime;

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

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
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
}
