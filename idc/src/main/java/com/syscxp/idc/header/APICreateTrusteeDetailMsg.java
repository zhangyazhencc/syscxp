package com.syscxp.idc.header;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.idc.trustee.TrusteeConstant;

import java.math.BigDecimal;

@Action(services = {TrusteeConstant.SERVICE_ID}, category = TrusteeConstant.ACTION_CATEGORY, names = {"create"})
public class APICreateTrusteeDetailMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String name;

    @APIParam(emptyString = false)
    private String trusteeUuid;

    @APIParam(emptyString = false)
    private BigDecimal cost;

    @APIParam(required = false)
    private String description;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrusteeUuid() {
        return trusteeUuid;
    }

    public void setTrusteeUuid(String trusteeUuid) {
        this.trusteeUuid = trusteeUuid;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
