package org.zstack.billing.header.identity.balance;


import org.zstack.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = DealDetailVO.class)
public class DealDetailInventory {

    private String uuid;
    private DealType type;
    private BigDecimal expend;
    private BigDecimal income;
    private DealWay dealWay;
    private DealState state;
    private Timestamp finishTime;
    private BigDecimal balance;
    private String accountUuid;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static DealDetailInventory valueOf(DealDetailVO vo) {
        DealDetailInventory inv = new DealDetailInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setState(vo.getState());
        inv.setType(vo.getType());
        inv.setBalance(vo.getBalance());
        inv.setDealWay(vo.getDealWay());
        inv.setExpend(vo.getExpend());
        inv.setFinishTime(vo.getFinishTime());
        inv.setIncome(vo.getIncome());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<DealDetailInventory> valueOf(Collection<DealDetailVO> vos) {
        List<DealDetailInventory> lst = new ArrayList<>(vos.size());
        for (DealDetailVO vo : vos) {
            lst.add(DealDetailInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public DealType getType() {
        return type;
    }

    public void setType(DealType type) {
        this.type = type;
    }

    public BigDecimal getExpend() {
        return expend;
    }

    public void setExpend(BigDecimal expend) {
        this.expend = expend;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public DealWay getDealWay() {
        return dealWay;
    }

    public void setDealWay(DealWay dealWay) {
        this.dealWay = dealWay;
    }

    public DealState getState() {
        return state;
    }

    public void setState(DealState state) {
        this.state = state;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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
