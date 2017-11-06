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
    private Long duration;
    private String alarmContent;
    private String status;
    private String accountUuid;
    private String smsContent;
    private String mailContent;
    private String ruleName;
    private String eventId;

    public static AlarmLogInventory valueOf(AlarmLogVO vo){
        AlarmLogInventory inv = new AlarmLogInventory();
        inv.setUuid(vo.getUuid());
        inv.setProductUuid(vo.getProductUuid());
        inv.setProductName(vo.getProductName());
        inv.setProductType(vo.getProductType());
        inv.setDuration(vo.getDuration());
        inv.setAlarmContent(vo.getAlarmContent());
        inv.setStatus(vo.getStatus());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setSmsContent(vo.getSmsContent());
        inv.setMailContent(vo.getMailContent());
        inv.setRuleName(vo.getRuleName());
        inv.setEventId(vo.getEventId());
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

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
