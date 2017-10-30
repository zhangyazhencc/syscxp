package com.syscxp.billing.header.balance;
import com.syscxp.header.billing.Category;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = AccountDiscountVO.class)
public class AccountDiscountInventory {

    private String uuid;

    private String accountUuid;

    private ProductType productType;

    private int discount;

    private Category category;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private String categoryName;

    private String productTypeName;

    public static AccountDiscountInventory valueOf(AccountDiscountVO vo) {
        AccountDiscountInventory inv = new AccountDiscountInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setDiscount(vo.getDiscount());
        inv.setProductType(vo.getProductType());
        inv.setCategory(vo.getCategory());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCategoryName(vo.getCategoryName());
        inv.setProductTypeName(vo.getProductTypeName());
        return inv;
    }

    public static List<AccountDiscountInventory> valueOf(Collection<AccountDiscountVO> vos) {
        List<AccountDiscountInventory> lst = new ArrayList<AccountDiscountInventory>(vos.size());
        for (AccountDiscountVO vo : vos) {
            lst.add(AccountDiscountInventory.valueOf(vo));
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

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }
}
