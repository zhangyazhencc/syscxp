package com.syscxp.billing.header.bill;

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
public class BillVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    private Timestamp timeStart;

    @Column
    private Timestamp timeEnd;

    @Column
    private BigDecimal totalDeductionPayCash;

    @Column
    private BigDecimal totalDeductionPayPresent;

    @Column
    private BigDecimal totalRefundIncomeCash;

    @Column
    private BigDecimal totalRefundIncomePresent;

    @Column
    private BigDecimal totalRechargeIncomeCash;

    @Column
    private BigDecimal totalRechargeIncomePresent;

    @Column
    private BigDecimal repay;

    @Column
    private Timestamp billDate;

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

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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

    public BigDecimal getTotalDeductionPayCash() {
        return totalDeductionPayCash;
    }

    public void setTotalDeductionPayCash(BigDecimal totalDeductionPayCash) {
        this.totalDeductionPayCash = totalDeductionPayCash;
    }

    public BigDecimal getTotalDeductionPayPresent() {
        return totalDeductionPayPresent;
    }

    public void setTotalDeductionPayPresent(BigDecimal totalDeductionPayPresent) {
        this.totalDeductionPayPresent = totalDeductionPayPresent;
    }

    public BigDecimal getTotalRefundIncomeCash() {
        return totalRefundIncomeCash;
    }

    public void setTotalRefundIncomeCash(BigDecimal totalRefundIncomeCash) {
        this.totalRefundIncomeCash = totalRefundIncomeCash;
    }

    public BigDecimal getTotalRefundIncomePresent() {
        return totalRefundIncomePresent;
    }

    public void setTotalRefundIncomePresent(BigDecimal totalRefundIncomePresent) {
        this.totalRefundIncomePresent = totalRefundIncomePresent;
    }

    public BigDecimal getTotalRechargeIncomeCash() {
        return totalRechargeIncomeCash;
    }

    public void setTotalRechargeIncomeCash(BigDecimal totalRechargeIncomeCash) {
        this.totalRechargeIncomeCash = totalRechargeIncomeCash;
    }

    public BigDecimal getTotalRechargeIncomePresent() {
        return totalRechargeIncomePresent;
    }

    public void setTotalRechargeIncomePresent(BigDecimal totalRechargeIncomePresent) {
        this.totalRechargeIncomePresent = totalRechargeIncomePresent;
    }

    public BigDecimal getRepay() {
        return repay;
    }

    public void setRepay(BigDecimal repay) {
        this.repay = repay;
    }

    public Timestamp getBillDate() {
        return billDate;
    }

    public void setBillDate(Timestamp billDate) {
        this.billDate = billDate;
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

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
