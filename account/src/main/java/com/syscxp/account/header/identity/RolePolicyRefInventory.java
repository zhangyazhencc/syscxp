package com.syscxp.account.header.identity;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = RolePolicyRefVO.class)
public class RolePolicyRefInventory {

    private String roleUuid;
    private String policyUuid;
    private Timestamp createDate;

    private PolicyInventory policy;

    public static RolePolicyRefInventory valueOf(RolePolicyRefVO vo) {
        RolePolicyRefInventory inv = new RolePolicyRefInventory();
        inv.setRoleUuid(vo.getRoleUuid());
        inv.setPolicyUuid(vo.getPolicyUuid());
        inv.setPolicy(PolicyInventory.valueOf(vo.getPolicy()));
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<RolePolicyRefInventory> valueOf(Collection<RolePolicyRefVO> vos) {
        List<RolePolicyRefInventory> invs = new ArrayList<RolePolicyRefInventory>();
        for (RolePolicyRefVO vo : vos) {
            invs.add(valueOf(vo));
        }
        return invs;
    }


    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public PolicyInventory getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyInventory policy) {
        this.policy = policy;
    }
}
