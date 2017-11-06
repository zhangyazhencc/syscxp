package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = MonitorTargetVO.class)
public class MonitorTargetInventory {
    private ProductType productType;
    private String targetName;
    private String targetValue;
    private String uuid;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static MonitorTargetInventory valueOf(MonitorTargetVO vo) {
        MonitorTargetInventory inv = new MonitorTargetInventory();
        inv.setUuid(vo.getUuid());
        inv.setProductType(vo.getProductType());
        inv.setTargetName(vo.getTargetName());
        inv.setTargetValue(vo.getTargetValue());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<MonitorTargetInventory> valueOf(Collection<MonitorTargetVO> vos) {
        List<MonitorTargetInventory> lst = new ArrayList<>(vos.size());
        for (MonitorTargetVO vo : vos) {
            lst.add(MonitorTargetInventory.valueOf(vo));
        }
        return lst;
    }



    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
