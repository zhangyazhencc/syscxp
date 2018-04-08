package com.syscxp.idc.header;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.idc.IdcConstant;

import java.math.BigDecimal;


@Action(services = {IdcConstant.SERVICE_ID}, category = IdcConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateIdcMsg extends APIMessage{

    @APIParam(emptyString = false)
    private String uuid;

    @APIParam(emptyString = false,required = false)
    private String name;

    @APIParam(required = false)
    private String description;

    @APIParam(emptyString = false,required = false)
    private String company;

    @APIParam(emptyString = false,required = false)
    private String contractNum;

    @APIParam(emptyString = false,required = false)
    private String nodeUuid;

    @APIParam(emptyString = false,required = false)
    private String nodeName;

    @APIParam(emptyString = false)
    private BigDecimal totalCost;

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getContractNum() {
        return contractNum;
    }

    public void setContractNum(String contractNum) {
        this.contractNum = contractNum;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
