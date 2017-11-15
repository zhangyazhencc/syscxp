package com.syscxp.billing.header.receipt;

import com.syscxp.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ReceiptVO.class)
//@ExpandedQueries({
//        @ExpandedQuery(expandedField = "receiptInfo", inventoryClass = ReceiptInfoInventory.class,
//                foreignKey = "receiptInfoUuid", expandedInventoryKey = "uuid"),
//        @ExpandedQuery(expandedField = "receiptPostAddress", inventoryClass = ReceiptPostAddressInventory.class,
//                foreignKey = "receiptAddressUuid", expandedInventoryKey = "uuid")
//})

public class ReceiptInventory {

    private String uuid;

    private BigDecimal total;

    private Timestamp applyTime;

    private ReceiptState state;

    private ReceiptPostAddressInventory receiptPostAddressInventory;

    private String accountUuid;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private ReceiptInfoInventory receiptInfoInventory;

    private String opMan;

    private String receiptNO;

    private String comment;

    public static ReceiptInventory valueOf(ReceiptVO vo) {
        ReceiptInventory inv = new ReceiptInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setState(vo.getState());
        inv.setApplyTime(vo.getApplyTime());
        if(vo.getReceiptPostAddressVO()!=null)
        inv.setReceiptPostAddressInventory(ReceiptPostAddressInventory.valueOf(vo.getReceiptPostAddressVO()));
        if(vo.getReceiptInfoVO()!=null)
        inv.setReceiptInfoInventory(ReceiptInfoInventory.valueOf(vo.getReceiptInfoVO()));
        inv.setTotal(vo.getTotal());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setOpMan(vo.getOpMan());
        inv.setReceiptNO(vo.getReceiptNO());
        inv.setComment(vo.getCommet());

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

    public ReceiptPostAddressInventory getReceiptPostAddressInventory() {
        return receiptPostAddressInventory;
    }

    public void setReceiptPostAddressInventory(ReceiptPostAddressInventory receiptPostAddressInventory) {
        this.receiptPostAddressInventory = receiptPostAddressInventory;
    }

    public ReceiptInfoInventory getReceiptInfoInventory() {
        return receiptInfoInventory;
    }

    public void setReceiptInfoInventory(ReceiptInfoInventory receiptInfoInventory) {
        this.receiptInfoInventory = receiptInfoInventory;
    }

    public String getOpMan() {
        return opMan;
    }

    public void setOpMan(String opMan) {
        this.opMan = opMan;
    }

    public String getReceiptNO() {
        return receiptNO;
    }

    public void setReceiptNO(String receiptNO) {
        this.receiptNO = receiptNO;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
