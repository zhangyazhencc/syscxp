package com.syscxp.tunnel.header.aliEdgeRouter;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = AliEdgeRouterConfigVO.class)
public class AliEdgeRouterConfigInventory {
    private String uuid;
    private String aliRegionId;
    private String physicalLineUuid;
    private String switchPortUuid;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static AliEdgeRouterConfigInventory valueOf(AliEdgeRouterConfigVO vo){
        AliEdgeRouterConfigInventory inv = new AliEdgeRouterConfigInventory();
        inv.setUuid(vo.getUuid());
        inv.setAliRegionId(vo.getAliRegionId());
        inv.setPhysicalLineUuid(vo.getPhysicalLineUuid());
        inv.setSwitchPortUuid(vo.getSwitchPortUuid());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }


    public static List<AliEdgeRouterConfigInventory> valueOf(Collection<AliEdgeRouterConfigVO> vos) {
        List<AliEdgeRouterConfigInventory> lst = new ArrayList<>(vos.size());
        for (AliEdgeRouterConfigVO vo : vos) {
            lst.add(AliEdgeRouterConfigInventory.valueOf(vo));
        }
        return lst;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAliRegionId() {
        return aliRegionId;
    }

    public void setAliRegionId(String aliRegionId) {
        this.aliRegionId = aliRegionId;
    }

    public String getPhysicalLineUuid() {
        return physicalLineUuid;
    }

    public void setPhysicalLineUuid(String physicalLineUuid) {
        this.physicalLineUuid = physicalLineUuid;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
