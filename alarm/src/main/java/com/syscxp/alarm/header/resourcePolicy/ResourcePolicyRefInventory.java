package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.query.ExpandedQueries;
import com.syscxp.header.query.ExpandedQuery;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ResourcePolicyRefVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "policy", inventoryClass = PolicyInventory.class,
                foreignKey = "policyUuid", expandedInventoryKey = "uuid"),
})
public class ResourcePolicyRefInventory {

    private String uuid;

    private String policyUuid;
    private String resourceUuid;
    private String productType;
    private PolicyInventory policyInventory;

    private Timestamp createDate;
    private Timestamp lastOpDate;


    public static ResourcePolicyRefInventory valueOf(ResourcePolicyRefVO vo) {
        ResourcePolicyRefInventory inv = new ResourcePolicyRefInventory();
        inv.setResourceUuid(vo.getResourceUuid());
        inv.setPolicyUuid(vo.getPolicyUuid());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setProductType(vo.getProductType().toString());

        return inv;
    }

    public static List<ResourcePolicyRefInventory> valueOf(Collection<ResourcePolicyRefVO> vos) {
        List<ResourcePolicyRefInventory> lst = new ArrayList<>(vos.size());
        for (ResourcePolicyRefVO vo : vos) {
            lst.add(ResourcePolicyRefInventory.valueOf(vo));
        }
        return lst;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
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

    public PolicyInventory getPolicyInventory() {
        return policyInventory;
    }

    public void setPolicyInventory(PolicyInventory policyInventory) {
        this.policyInventory = policyInventory;
    }
}
