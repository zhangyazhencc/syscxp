package com.syscxp.header.vpn.vpn;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = VpnCertVO.class)
public class VpnCertInventory {
    private String uuid;
    private String name;
    private String accountUuid;
    private String description;
    private Integer vpnNum;
    private Integer version;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static VpnCertInventory valueOf(VpnCertVO vo) {
        VpnCertInventory inv = new VpnCertInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setDescription(vo.getDescription());
        inv.setVersion(vo.getVersion());
        inv.setVpnNum(vo.getVpnNum());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<VpnCertInventory> valueOf(Collection<VpnCertVO> vos) {
        List<VpnCertInventory> invs = new ArrayList<>();
        for (VpnCertVO vo : vos) {
            invs.add(VpnCertInventory.valueOf(vo));
        }

        return invs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public Integer getVpnNum() {
        return vpnNum;
    }

    public void setVpnNum(Integer vpnNum) {
        this.vpnNum = vpnNum;
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
