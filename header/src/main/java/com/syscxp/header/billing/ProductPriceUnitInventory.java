package com.syscxp.header.billing;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ProductPriceUnitVO.class)
public class ProductPriceUnitInventory {

    private String uuid;

    private String productCategoryUuid;

    private String areaCode;

    private String areaName;

    private String lineCode;

    private String lineName;

    private String configCode;

    private String configName;

    private int unitPrice;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    private int discount;
    private ProductCategoryVO productCategoryVO;

    public static ProductPriceUnitInventory valueOf(ProductPriceUnitVO vo) {
        ProductPriceUnitInventory inv = new ProductPriceUnitInventory();
        inv.setUuid(vo.getUuid());

        inv.setAreaCode(vo.getAreaCode());
        inv.setAreaName(vo.getAreaName());
        inv.setLineCode(vo.getLineCode());
        inv.setLineName(vo.getLineName());
        inv.setConfigCode(vo.getConfigCode());
        inv.setConfigName(vo.getConfigName());
        inv.setUnitPrice(vo.getUnitPrice());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setProductCategoryUuid(vo.getProductCategoryUuid());
        if(vo.getProductCategoryVO()!=null)
        inv.setProductCategoryVO(vo.getProductCategoryVO());

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


    public String getProductCategoryUuid() {
        return productCategoryUuid;
    }

    public void setProductCategoryUuid(String productCategoryUuid) {
        this.productCategoryUuid = productCategoryUuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
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

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public ProductCategoryVO getProductCategoryVO() {
        return productCategoryVO;
    }

    public void setProductCategoryVO(ProductCategoryVO productCategoryVO) {
        this.productCategoryVO = productCategoryVO;
    }
}
