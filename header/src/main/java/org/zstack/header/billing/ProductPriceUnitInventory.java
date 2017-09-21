package org.zstack.header.billing;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ProductPriceUnitVO.class)
public class ProductPriceUnitInventory {

    private String uuid;

    private String productName;

    private ProductType productType;

    private Category category;

    private String config;

    private Integer priceUnit;

    private String comment;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private Integer discharge  =100;

    private String categoryName;

    private String productTypeName;

    public static ProductPriceUnitInventory valueOf(ProductPriceUnitVO vo) {
        ProductPriceUnitInventory inv = new ProductPriceUnitInventory();
        inv.setUuid(vo.getUuid());
        inv.setProductName(vo.getProductName());
        inv.setProductType(vo.getProductType());
        inv.setCategory(vo.getCategory());
        inv.setPriceUnit(vo.getPriceUnit());
        inv.setConfig(vo.getConfig());
        inv.setComment(vo.getComment());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCategoryName(vo.getCategoryName());
        inv.setProductTypeName(vo.getProductTypeName());
        return inv;
    }

    public static List<ProductPriceUnitInventory> valueOf(Collection<ProductPriceUnitVO> vos) {
        List<ProductPriceUnitInventory> lst = new ArrayList<ProductPriceUnitInventory>(vos.size());
        for (ProductPriceUnitVO vo : vos) {
            lst.add(ProductPriceUnitInventory.valueOf(vo));
        }
        return lst;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Integer getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Integer priceUnit) {
        this.priceUnit = priceUnit;
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

    public Integer getDischarge() {
        return discharge;
    }

    public void setDischarge(Integer discharge) {
        this.discharge = discharge;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
