package com.syscxp.header.vpn.vpn;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Inventory(mappingVOClass = VpnSystemVO.class)
public class VpnSystemInventory {

    private String uuid;
    private Map<String, Object> vpn;
    private Map<String, Object> system;
    private Map<String, Object> tap;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnSystemInventory valueOf(VpnSystemVO vo) {
        VpnSystemInventory inv = new VpnSystemInventory();
        inv.setUuid(vo.getUuid());
        inv.setSystem(vo.getSystem());
        inv.setTap(vo.getTap());
        inv.setVpn(vo.getVpn());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<VpnSystemInventory> valueOf(Collection<VpnSystemVO> vos) {
        List<VpnSystemInventory> invs = new ArrayList<VpnSystemInventory>(vos.size());
        for (VpnSystemVO vo : vos) {
            invs.add(VpnSystemInventory.valueOf(vo));
        }
        return invs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, Object> getVpn() {
        return vpn;
    }

    public void setVpn(Map<String, Object> vpn) {
        this.vpn = vpn;
    }

    public Map<String, Object> getSystem() {
        return system;
    }

    public void setSystem(Map<String, Object> system) {
        this.system = system;
    }

    public Map<String, Object> getTap() {
        return tap;
    }

    public void setTap(Map<String, Object> tap) {
        this.tap = tap;
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
