package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.BaseVO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table
@Entity
public class PolicyRegulationRefVO extends BaseVO{

    @Column
    private String policyUuid;

    @Column
    private String regulationUuid;

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public String getRegulationUuid() {
        return regulationUuid;
    }

    public void setRegulationUuid(String regulationUuid) {
        this.regulationUuid = regulationUuid;
    }
}
