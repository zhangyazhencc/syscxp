package com.syscxp.billing.header.bill;


import com.syscxp.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = BillVO.class)
public class BillInventory {

    private String uuid;

    private String accountUuid;

    private Timestamp timeStart;

    private Timestamp timeEnd;

    private BigDecimal totalDeductionPayCash;

    private BigDecimal totalDeductionPayPresent;

    private BigDecimal totalRefundIncomeCash;

    private BigDecimal totalRefundIncomePresent;

    private BigDecimal totalRechargeIncomeCash;

    private BigDecimal totalRechargeIncomePresent;

    private BigDecimal repay;

    private Timestamp billDate;

    private BigDecimal cashBalance;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private List<MonetaryResult> bills;

    public static BillInventory valueOf(BillVO vo) {
        BillInventory inv = new BillInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setBillDate(vo.getBillDate());
        inv.setRepay(vo.getRepay());
        inv.setTimeEnd(vo.getTimeEnd());
        inv.setTimeStart(vo.getTimeStart());
        inv.setCashBalance(vo.getCashBalance());
        inv.setTotalDeductionPayCash(vo.getTotalDeductionPayCash());
        inv.setTotalDeductionPayPresent(vo.getTotalDeductionPayPresent());
        inv.setTotalRechargeIncomeCash(vo.getTotalRechargeIncomeCash());
        inv.setTotalRechargeIncomePresent(vo.getTotalRechargeIncomePresent());
        inv.setTotalRefundIncomeCash(vo.getTotalRefundIncomeCash());
        inv.setTotalRefundIncomePresent(vo.getTotalRefundIncomePresent());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<BillInventory> valueOf(Collection<BillVO> vos) {
        List<BillInventory> lst = new ArrayList<>(vos.size());
        for (BillVO vo : vos) {
            lst.add(BillInventory.valueOf(vo));
        }
        return lst;
    }

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

    public List<MonetaryResult> getBills() {
        return bills;
    }

    public void setBills(List<MonetaryResult> bills) {
        this.bills = bills;
    }
}
