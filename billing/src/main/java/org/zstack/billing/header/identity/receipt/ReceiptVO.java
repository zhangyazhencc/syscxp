package org.zstack.billing.header.identity.receipt;

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
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="receiptInfoUuid")
    private ReceiptInfoVO receiptInfoVO;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="receiptAddressUuid")
    private ReceiptPostAddressVO receiptPostAddressVO;

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
}
