package com.syscxp.billing.header.sla;

import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class SLALogVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    private String productUuid;

    @Column
    private Timestamp timeStart;

    @Column
    private Timestamp timeEnd;

    @Column
    private BigDecimal slaPrice;

    @Column
    private int duration;

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

    public Timestamp getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Timestamp timeStart) {
        this.timeStart = timeStart;
    }

    public Timestamp getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Timestamp timeEnd) {
        this.timeEnd = timeEnd;
    }

    public BigDecimal getSlaPrice() {
        return slaPrice;
    }

    public void setSlaPrice(BigDecimal slaPrice) {
        this.slaPrice = slaPrice;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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
    private void update(){
        this.lastOpDate = null;
    }
}
