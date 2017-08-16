package org.zstack.account.header.identity;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = PermissionVO.class)

public class AuthorityInventory {

    private String uuid;
    private String name;
    private String authority;
    private String description;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static AuthorityInventory valueOf(PermissionVO vo) {
        AuthorityInventory inv = new AuthorityInventory();
        inv.setName(vo.getName());
        inv.setUuid(vo.getUuid());
        inv.setAuthority(vo.getAuthority());
        inv.setDescription(vo.getDescription());
        return inv;
    }

    public static List<AuthorityInventory> valueOf(Collection<PermissionVO> vos) {
        List<AuthorityInventory> invs = new ArrayList<AuthorityInventory>();
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

    public String getAuthority() {
        return authority;
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

    public void setAuthority(String authority) {
        this.authority = authority;
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
