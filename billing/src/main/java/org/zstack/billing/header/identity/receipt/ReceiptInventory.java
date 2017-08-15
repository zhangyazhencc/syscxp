package org.zstack.billing.header.identity.receipt;

import org.zstack.billing.header.identity.ReceiptState;
import org.zstack.billing.header.identity.ReceiptType;
import org.zstack.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ReceiptVO.class)
public class ReceiptInventory {

    private String uuid;

    private BigDecimal total;

    private ReceiptType type;

    private String title;

    private Timestamp applyTime;

    private ReceiptState state;

    private String receiptInfoUuid;

    private String receiptAddressUuid;

    private String accountUuid;

    private String receiptNumber;

    private String comment;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static ReceiptInventory valueOf(ReceiptVO vo) {
        ReceiptInventory inv = new ReceiptInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setState(vo.getState());
        inv.setType(vo.getType());
        inv.setApplyTime(vo.getApplyTime());
        inv.setComment(vo.getComment());
        inv.setReceiptAddressUuid(vo.getReceiptAddressUuid());
        inv.setReceiptInfoUuid(vo.getReceiptInfoUuid());
        inv.setReceiptNumber(vo.getReceiptNumber());
        inv.setTitle(vo.getTitle());
        inv.setTotal(vo.getTotal());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<ReceiptInventory> valueOf(Collection<ReceiptVO> vos) {
        List<ReceiptInventory> lst = new ArrayList<>(vos.size());
        for (ReceiptVO vo : vos) {
            lst.add(ReceiptInventory.valueOf(vo));
        }
        return lst;
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
