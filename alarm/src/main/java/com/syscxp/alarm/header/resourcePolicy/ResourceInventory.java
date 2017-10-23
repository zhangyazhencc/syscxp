package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ResourceVO.class)
public class ResourceInventory {

    private String uuid;
    private ProductType productType;

    private String productUuid;

    private String productName;

    private String description;

    private String networkSegmentA;

    private String networkSegmentB;

    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static ResourceInventory valueOf(ResourceVO vo) {
        ResourceInventory inv = new ResourceInventory();
        inv.setUuid(vo.getUuid());
        inv.setDescription(vo.getDescription());
        inv.setNetworkSegmentA(vo.getNetworkSegmentA());
        inv.setNetworkSegmentB(vo.getNetworkSegmentB());
        inv.setProductName(vo.getProductName());
        inv.setProductType(vo.getProductType());
        inv.setProductUuid(vo.getProductUuid());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<ResourceInventory> valueOf(Collection<ResourceVO> vos) {
        List<ResourceInventory> lst = new ArrayList<>(vos.size());
        for (ResourceVO vo : vos) {
            lst.add(ResourceInventory.valueOf(vo));
        }
        return lst;
    }



    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNetworkSegmentA() {
        return networkSegmentA;
    }

    public void setNetworkSegmentA(String networkSegmentA) {
        this.networkSegmentA = networkSegmentA;
    }

    public String getNetworkSegmentB() {
        return networkSegmentB;
    }

    public void setNetworkSegmentB(String networkSegmentB) {
        this.networkSegmentB = networkSegmentB;
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
