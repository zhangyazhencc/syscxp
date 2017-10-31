package com.syscxp.billing.header.receipt;

import com.syscxp.header.search.SqlTrigger;
import com.syscxp.header.search.TriggerIndex;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
@SqlTrigger
public class ReceiptVO {

    @Id
    @Column
    private String uuid;

    @Column
    private BigDecimal total;

    @Column
    private Timestamp applyTime;

    @Column
    @Enumerated(EnumType.STRING)
    private ReceiptState state;

    @Column
    private String accountUuid;

    @Column
    private String commet;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @Column
    private String receiptNO;

    @Column
    private String opMan;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="receiptInfoUuid",insertable = false,updatable = false)
    private ReceiptInfoVO receiptInfoVO;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="receiptAddressUuid",insertable = false,updatable = false)
    private ReceiptPostAddressVO receiptPostAddressVO;

    @Column
    private String receiptInfoUuid;
    @Column
    private String receiptAddressUuid;

    public String getReceiptInfoUuid() {
        return receiptInfoUuid;
    }

    public void setReceiptInfoUuid(String receiptInfoUuid) {
        this.receiptInfoUuid = receiptInfoUuid;
    }

    public String getReceiptAddressUuid() {
        return receiptAddressUuid;
    }

    public void setReceiptAddressUuid(String receiptAddressUuid) {
        this.receiptAddressUuid = receiptAddressUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Timestamp getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Timestamp applyTime) {
        this.applyTime = applyTime;
    }

    public ReceiptState getState() {
        return state;
    }

    public void setState(ReceiptState state) {
        this.state = state;
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

    public ReceiptInfoVO getReceiptInfoVO() {
        return receiptInfoVO;
    }

    public void setReceiptInfoVO(ReceiptInfoVO receiptInfoVO) {
        this.receiptInfoVO = receiptInfoVO;
    }

    public ReceiptPostAddressVO getReceiptPostAddressVO() {
        return receiptPostAddressVO;
    }

    public void setReceiptPostAddressVO(ReceiptPostAddressVO receiptPostAddressVO) {
        this.receiptPostAddressVO = receiptPostAddressVO;
    }

    public String getCommet() {
        return commet;
    }

    public void setCommet(String commet) {
        this.commet = commet;
    }

    @PreUpdate
    void preUpdate() {
        lastOpDate = null;
    }

    public String getReceiptNO() {
        return receiptNO;
    }

    public void setReceiptNO(String receiptNO) {
        this.receiptNO = receiptNO;
    }

    public String getOpMan() {
        return opMan;
    }

    public void setOpMan(String opMan) {
        this.opMan = opMan;
    }
}
