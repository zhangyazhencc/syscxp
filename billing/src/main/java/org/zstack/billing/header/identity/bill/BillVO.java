package org.zstack.billing.header.identity.bill;

import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;

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
    private BigDecimal totolPayCash;

    @Column
    private BigDecimal totalPayPresent;

    @Column
    private BigDecimal totalIncomeCash;

    @Column
    private BigDecimal totalIncomePresent;

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

    public BigDecimal getTotolPayCash() {
        return totolPayCash;
    }

    public void setTotolPayCash(BigDecimal totolPayCash) {
        this.totolPayCash = totolPayCash;
    }

    public BigDecimal getTotalPayPresent() {
        return totalPayPresent;
    }

    public void setTotalPayPresent(BigDecimal totalPayPresent) {
        this.totalPayPresent = totalPayPresent;
    }

    public BigDecimal getTotalIncomeCash() {
        return totalIncomeCash;
    }

    public void setTotalIncomeCash(BigDecimal totalIncomeCash) {
        this.totalIncomeCash = totalIncomeCash;
    }

    public BigDecimal getTotalIncomePresent() {
        return totalIncomePresent;
    }

    public void setTotalIncomePresent(BigDecimal totalIncomePresent) {
        this.totalIncomePresent = totalIncomePresent;
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
