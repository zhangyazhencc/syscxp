package com.syscxp.account.header.identity;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = RoleVO.class)

public class RoleInventory {

    private String name;
    private String uuid;
    private String accountUuid;
    private String description;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    private List<PolicyInventory> permissions;

    public static RoleInventory valueOf(RoleVO vo) {
        RoleInventory inv = new RoleInventory();
        inv.setName(vo.getName());
        inv.setUuid(vo.getUuid());
        inv.setDescription(vo.getDescription());
        inv.setPermissions(PolicyInventory.valueOf(vo.getPolicySet()));
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<RoleInventory> valueOf(Collection<RoleVO> vos) {
        List<RoleInventory> invs = new ArrayList<RoleInventory>();
        for (RoleVO vo : vos) {
            invs.add(valueOf(vo));
        }
        return invs;
    }



    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<PolicyInventory> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PolicyInventory> permissions) {
        this.permissions = permissions;
    }
}
