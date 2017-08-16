package org.zstack.account.header.identity;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = PermissionVO.class)

public class PermissionInventory {

    private String uuid;
    private String name;
    private String policy;
    private String description;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static PermissionInventory valueOf(PermissionVO vo) {
        PermissionInventory inv = new PermissionInventory();
        inv.setName(vo.getName());
        inv.setUuid(vo.getUuid());
        inv.setPolicy(vo.getPolicy());
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

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getPolicy() {
        return policy;
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

    public void setPolicy(String policy) {
        this.policy = policy;
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
