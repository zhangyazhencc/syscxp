package com.syscxp.header.vpn.vpn;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnMotifyRecordVO.class)
public class VpnMotifyRecordInventory {
    private String uuid;
    private String resourceUuid;
    private String resourceType;
    private String opUserUuid;
    private String opAccountUuid;
    private String motifyType;
    private Timestamp createDate;

    public static VpnMotifyRecordInventory valueOf(VpnMotifyRecordVO vo) {
        VpnMotifyRecordInventory inv = new VpnMotifyRecordInventory();
        inv.setUuid(vo.getUuid());
        inv.setResourceUuid(vo.getResourceUuid());
        inv.setResourceType(vo.getResourceType());
        inv.setOpUserUuid(vo.getOpUserUuid());
        inv.setMotifyType(vo.getMotifyType());
        inv.setOpAccountUuid(vo.getOpAccountUuid());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }
    public static List<VpnMotifyRecordInventory> valueOf(Collection<VpnMotifyRecordVO> vos) {
        List<VpnMotifyRecordInventory> invs = new ArrayList<>();
        for (VpnMotifyRecordVO vo : vos) {
            invs.add(VpnMotifyRecordInventory.valueOf(vo));
        }

        return invs;
    }
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getOpUserUuid() {
        return opUserUuid;
    }

    public void setOpUserUuid(String opUserUuid) {
        this.opUserUuid = opUserUuid;
    }

    public String getOpAccountUuid() {
        return opAccountUuid;
    }

    public void setOpAccountUuid(String opAccountUuid) {
        this.opAccountUuid = opAccountUuid;
    }

    public String getMotifyType() {
        return motifyType;
    }

    public void setMotifyType(String motifyType) {
        this.motifyType = motifyType;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
