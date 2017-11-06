package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.contact.NotifyWayVO;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Inventory(mappingVOClass = PolicyVO.class)
public class PolicyInventory {

    private String uuid;
    private ProductType productType;
    private String name;
    private String description;
    private Long bindResources;
    private Timestamp createDate;
    private Timestamp lastOpDate;
    private Set<RegulationVO> regulationVOS;
    private String accountUuid;

    public static PolicyInventory valueOf(PolicyVO vo) {
        PolicyInventory inv = new PolicyInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setBindResources(vo.getBindResources());
        inv.setRegulationVOS(vo.getRegulationVOSet());
        inv.setProductType(vo.getProductType());
        inv.setDescription(vo.getDescription());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setAccountUuid(vo.getAccountUuid());

        return inv;
    }

    public static List<PolicyInventory> valueOf(Collection<PolicyVO> vos) {
        List<PolicyInventory> lst = new ArrayList<>(vos.size());
        for (PolicyVO vo : vos) {
            lst.add(PolicyInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getBindResources() {
        return bindResources;
    }

    public void setBindResources(Long bindResources) {
        this.bindResources = bindResources;
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

    public Set<RegulationVO> getRegulationVOS() {
        return regulationVOS;
    }

    public void setRegulationVOS(Set<RegulationVO> regulationVOS) {
        this.regulationVOS = regulationVOS;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
