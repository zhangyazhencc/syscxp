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

    private BigDecimal totolPayCash;

    private BigDecimal totalPayPresent;

    private BigDecimal totalIncomeCash;

    private BigDecimal totalIncomePresent;

    private BigDecimal repay;

    private Timestamp billDate;

    private BigDecimal cashBalance;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private List<Monetary> bills;

    public static BillInventory valueOf(BillVO vo) {
        BillInventory inv = new BillInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setBillDate(vo.getBillDate());
        inv.setRepay(vo.getRepay());
        inv.setTimeEnd(vo.getTimeEnd());
        inv.setTimeStart(vo.getTimeStart());
        inv.setCashBalance(vo.getCashBalance());
        inv.setTotalIncomeCash(vo.getTotalIncomeCash());
        inv.setTotalIncomePresent(vo.getTotalIncomePresent());
        inv.setTotalPayPresent(vo.getTotalPayPresent());
        inv.setTotolPayCash(vo.getTotolPayCash());
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

    public List<Monetary> getBills() {
        return bills;
    }

    public void setBills(List<Monetary> bills) {
        this.bills = bills;
    }
}
