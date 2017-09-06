package org.zstack.billing.header.balance;

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
public class DealDetailVO {

    @Id
    @Column
    private String uuid;

    @Column
    @Enumerated(EnumType.STRING)
    private DealType type;

    @Column
    private BigDecimal expend;

    @Column
    private BigDecimal income;

    @Column
    @Enumerated(EnumType.STRING)
    private DealWay dealWay;

    @Column
    @Enumerated(EnumType.STRING)
    private DealState state;

    @Column
    private Timestamp finishTime;

    @Column
    private BigDecimal balance;

    @Column
    private String accountUuid;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @Column
    private String outTradeNO;
    @Column
    private String tradeNO;

    @Column
    private String opAccountUuid;

    public String getOutTradeNO() {
        return outTradeNO;
    }

    public void setOutTradeNO(String outTradeNO) {
        this.outTradeNO = outTradeNO;
    }

    public String getTradeNO() {
        return tradeNO;
    }

    public void setTradeNO(String tradeNO) {
        this.tradeNO = tradeNO;
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

    public String getOpAccountUuid() {
        return opAccountUuid;
    }

    public void setOpAccountUuid(String opAccountUuid) {
        this.opAccountUuid = opAccountUuid;
    }
}
