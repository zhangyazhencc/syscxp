package com.syscxp.header.billing;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.*;

import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class AccountBalanceVO {

    @Id
    @Column
    private String uuid;

    @Column
    private BigDecimal presentBalance;

    @Column
    private BigDecimal creditPoint;

    @Column
    private BigDecimal cashBalance;

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

    public BigDecimal getPresentBalance() {
        return presentBalance;
    }

    public void setPresentBalance(BigDecimal presentBalance) {
        this.presentBalance = presentBalance;
    }

    public BigDecimal getCreditPoint() {
        return creditPoint;
    }

    public void setCreditPoint(BigDecimal creditPoint) {
        this.creditPoint = creditPoint;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @PreUpdate
    void preUpdate() {
        lastOpDate = null;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

}
