package org.zstack.billing.header.identity.renew;

import org.zstack.billing.header.identity.balance.ProductChargeModel;
import org.zstack.billing.header.identity.balance.ProductType;
import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = RenewVO.class)
public class RenewInventory {

    private String uuid;

    private String accountUuid;

    private String productName;

    private ProductType productType;

    private ProductChargeModel productChargeModel;

    private String productDescription;

    private String productUuid;

    private int duration;

    private Timestamp expiredDate;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private String productUnitPriceUuid;

    public static RenewInventory valueOf(RenewVO vo) {
        RenewInventory inv = new RenewInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setProductChargeModel(vo.getProductChargeModel());
        inv.setProductName(vo.getProductName());
        inv.setProductType(vo.getProductType());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setProductUuid(vo.getProductUuid());
        inv.setDuration(vo.getDuration());
        inv.setProductUnitPriceUuid(vo.getProductUnitPriceUuid());
        return inv;
    }

    public static List<RenewInventory> valueOf(Collection<RenewVO> vos) {
        List<RenewInventory> lst = new ArrayList<>(vos.size());
        for (RenewVO vo : vos) {
            lst.add(RenewInventory.valueOf(vo));
        }
        return lst;
    }

    public String getProductUnitPriceUuid() {
        return productUnitPriceUuid;
    }

    public void setProductUnitPriceUuid(String productUnitPriceUuid) {
        this.productUnitPriceUuid = productUnitPriceUuid;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Timestamp getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
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
