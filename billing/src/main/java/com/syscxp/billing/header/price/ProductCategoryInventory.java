package com.syscxp.billing.header.price;


import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ProductCategoryEO.class)
public class ProductCategoryInventory {

    private String uuid;

    private String code;

    private String name;

    private String productTypeCode;

    private String productTypeName;

    private Timestamp createDate;

    private Timestamp lastOpDate;


    public static ProductCategoryInventory valueOf(ProductCategoryEO vo) {
        ProductCategoryInventory inv = new ProductCategoryInventory();
        inv.setUuid(vo.getUuid());
        inv.setCode(vo.getCode());
        inv.setName(vo.getName());
        inv.setProductTypeCode(vo.getProductTypeCode());
        inv.setProductTypeName(vo.getProductTypeName());
        inv.setUuid(vo.getUuid());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<ProductCategoryInventory> valueOf(Collection<ProductCategoryEO> vos) {
        List<ProductCategoryInventory> lst = new ArrayList<>(vos.size());
        for (ProductCategoryEO vo : vos) {
            lst.add(ProductCategoryInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
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
