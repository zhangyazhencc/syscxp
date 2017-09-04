package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.PermissionType;
import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = PermissionVO.class)

public class PermissionInventory {

    private String uuid;
    private String name;
    private String permission;
    private String description;
    private PermissionType type;
    private AccountType level;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static PermissionInventory valueOf(PermissionVO vo) {
        PermissionInventory inv = new PermissionInventory();
        inv.setName(vo.getName());
        inv.setUuid(vo.getUuid());
        inv.setType(vo.getType());
        inv.setLevel(vo.getLevel());
        inv.setPermission(vo.getPermission());
        inv.setDescription(vo.getDescription());
        return inv;
    }

    public static List<PermissionInventory> valueOf(Collection<PermissionVO> vos) {
        List<PermissionInventory> invs = new ArrayList<PermissionInventory>();
        for (PermissionVO vo : vos) {
            invs.add(valueOf(vo));
        }
        return invs;
    }

    public PermissionType getType() {
        return type;
    }

    public void setType(PermissionType type) {
        this.type = type;
    }

    public AccountType getLevel() {
        return level;
    }

    public void setLevel(AccountType level) {
        this.level = level;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
