package org.zstack.vpn.header.vpn;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnMotifyRecordVO.class)
public class VpnMotifyRecordInventory {
    private String uuid;
    private String vpnUuid;
    private String opAccountUuid;
    private MotifyType motifyType;
    private Timestamp createDate;

    public static VpnMotifyRecordInventory valueOf(VpnMotifyRecordVO vo) {
        VpnMotifyRecordInventory inv = new VpnMotifyRecordInventory();
        inv.setUuid(vo.getUuid());
        inv.setVpnUuid(vo.getVpnUuid());
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

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }

    public String getOpAccountUuid() {
        return opAccountUuid;
    }

    public void setOpAccountUuid(String opAccountUuid) {
        this.opAccountUuid = opAccountUuid;
    }

    public MotifyType getMotifyType() {
        return motifyType;
    }

    public void setMotifyType(MotifyType motifyType) {
        this.motifyType = motifyType;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
