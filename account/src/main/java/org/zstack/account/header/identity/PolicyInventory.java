package org.zstack.account.header.identity;

import org.zstack.header.identity.PolicyStatement;
import org.zstack.header.query.ExpandedQueries;
import org.zstack.header.query.ExpandedQuery;
import org.zstack.header.search.Inventory;
import org.zstack.utils.gson.JSONObjectUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Inventory(mappingVOClass = PolicyVO.class)

public class PolicyInventory {

    private String name;
    private String uuid;
    private String accountUuid;
    private String description;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    private Set<PermissionVO> permissions;

    public static PolicyInventory valueOf(PolicyVO vo) {
        PolicyInventory inv = new PolicyInventory();
        inv.setName(vo.getName());
        inv.setUuid(vo.getUuid());
        inv.setDescription(vo.getDescription());
        inv.setPermissions(vo.getPermissions());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<PolicyInventory> valueOf(Collection<PolicyVO> vos) {
        List<PolicyInventory> invs = new ArrayList<PolicyInventory>();
        for (PolicyVO vo : vos) {
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

    public Set<PermissionVO> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionVO> permissions) {
        this.permissions = permissions;
    }
}
