package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO;
import com.syscxp.header.billing.ProductType;

import javax.persistence.*;
import java.util.Set;

@Table
@Entity
public class PolicyVO extends BaseVO {

    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private long bindResources;

    @Column
    private String accountUuid;

    @OneToMany(fetch =FetchType.EAGER,cascade = CascadeType.REMOVE)
    @JoinColumn(name="policyUuid")
    private Set<RegulationVO> regulationVOSet;

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

    public long getBindResources() {
        return bindResources;
    }

    public void setBindResources(long bindResources) {
        this.bindResources = bindResources;
    }

    public Set<RegulationVO> getRegulationVOSet() {
        return regulationVOSet;
    }

    public void setRegulationVOSet(Set<RegulationVO> regulationVOSet) {
        this.regulationVOSet = regulationVOSet;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

}
