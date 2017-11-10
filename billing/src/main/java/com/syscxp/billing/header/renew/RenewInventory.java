package com.syscxp.billing.header.renew;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.Inventory;

import java.math.BigDecimal;
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

    private String descriptionData;

    private String productUuid;

    private int duration;

    private boolean isRenewAuto;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private BigDecimal priceOneMonth;

    private Timestamp expiredTime;

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
        inv.setPriceOneMonth(vo.getPriceOneMonth());
        inv.setDescriptionData(vo.getDescriptionData());
        inv.setRenewAuto(vo.isRenewAuto());
        inv.setExpiredTime(vo.getExpiredTime());
        return inv;
    }

    public static List<RenewInventory> valueOf(Collection<RenewVO> vos) {
        List<RenewInventory> lst = new ArrayList<>(vos.size());
        for (RenewVO vo : vos) {
            lst.add(RenewInventory.valueOf(vo));
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

    public String getDescriptionData() {
        return descriptionData;
    }

    public void setDescriptionData(String descriptionData) {
        this.descriptionData = descriptionData;
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

    public BigDecimal getPriceOneMonth() {
        return priceOneMonth;
    }

    public void setPriceOneMonth(BigDecimal priceOneMonth) {
        this.priceOneMonth = priceOneMonth;
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

    public boolean isRenewAuto() {
        return isRenewAuto;
    }

    public void setRenewAuto(boolean renewAuto) {
        isRenewAuto = renewAuto;
    }

    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Timestamp expiredTime) {
        this.expiredTime = expiredTime;
    }
}
