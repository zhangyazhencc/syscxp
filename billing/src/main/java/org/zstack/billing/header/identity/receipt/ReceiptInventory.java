package org.zstack.billing.header.identity.receipt;

import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ReceiptVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "receiptInfo", inventoryClass = ReceiptInfoInventory.class,
                foreignKey = "receiptInfoUuid", expandedInventoryKey = "uuid"),
})

public class ReceiptInventory {

    private String uuid;

    private BigDecimal total;

    private Timestamp applyTime;

    private ReceiptState state;

    private String receiptInfoUuid;

    private String receiptAddressUuid;

    private String accountUuid;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private ReceiptInfoVO receiptInfoVO;

    public static ReceiptInventory valueOf(ReceiptVO vo) {
        ReceiptInventory inv = new ReceiptInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setState(vo.getState());
        inv.setApplyTime(vo.getApplyTime());
        inv.setReceiptAddressUuid(vo.getReceiptAddressUuid());
        inv.setReceiptInfoUuid(vo.getReceiptInfoUuid());
        inv.setTotal(vo.getTotal());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setReceiptInfoVO(vo.getReceiptInfoVO());

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
}
