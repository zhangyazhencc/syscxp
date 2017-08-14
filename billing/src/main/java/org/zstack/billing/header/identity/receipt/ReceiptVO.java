package org.zstack.billing.header.identity.receipt;

import org.zstack.billing.header.identity.ReceiptState;
import org.zstack.billing.header.identity.ReceiptType;
import org.zstack.header.search.SqlTrigger;
import org.zstack.header.search.TriggerIndex;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

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
    private ReceiptType type;

    @Column
    private String title;

    @Column
    private Timestamp applyTime;

    @Column
    private ReceiptState state;

    @Column
    private String receiptInfoUuid;

    @Column
    private String receiptAddressUuid;

    @Column
    private String accountUuid;

    @Column
    private String receiptNumber;

    @Column
    private String comment;

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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public ReceiptType getType() {
        return type;
    }

    public void setType(ReceiptType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
