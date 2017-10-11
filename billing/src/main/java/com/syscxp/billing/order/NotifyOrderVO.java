package com.syscxp.billing.order;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class NotifyOrderVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String orderUuid;

    @Column
    @Enumerated(EnumType.STRING)
    private NotifyOrderStatus status;

    @Column
    private int notifyTimes;

    @Column
    private String url;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public NotifyOrderStatus getStatus() {
        return status;
    }

    public void setStatus(NotifyOrderStatus status) {
        this.status = status;
    }

    public int getNotifyTimes() {
        return notifyTimes;
    }

    public void setNotifyTimes(int notifyTimes) {
        this.notifyTimes = notifyTimes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    @PreUpdate
    void preUpdate() {
        lastOpDate = null;
    }
}
