package org.zstack.billing.header.balance;
import org.zstack.billing.header.order.Category;
import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = AccountDischargeVO.class)
public class AccountDischargeInventory {

    private String uuid;

    private String accountUuid;

    private ProductType productType;

    private int disCharge;

    private Category category;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static AccountDischargeInventory valueOf(AccountDischargeVO vo) {
        AccountDischargeInventory inv = new AccountDischargeInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setDisCharge(vo.getDisCharge());
        inv.setProductType(vo.getProductType());
        inv.setCategory(vo.getCategory());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<AccountDischargeInventory> valueOf(Collection<AccountDischargeVO> vos) {
        List<AccountDischargeInventory> lst = new ArrayList<AccountDischargeInventory>(vos.size());
        for (AccountDischargeVO vo : vos) {
            lst.add(AccountDischargeInventory.valueOf(vo));
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

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public int getDisCharge() {
        return disCharge;
    }

    public void setDisCharge(int disCharge) {
        this.disCharge = disCharge;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
