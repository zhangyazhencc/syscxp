package com.syscxp.idc.header.trustee;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.idc.trustee.TrusteeConstant;

import java.math.BigDecimal;
import java.util.Map;

@Action(services = {TrusteeConstant.SERVICE_ID}, category = TrusteeConstant.ACTION_CATEGORY, names = {"create"})
public class APICreateTrusteeMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String name;

    @APIParam(required = false)
    private String description;

    @APIParam(emptyString = false)
    private String accountName;

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(emptyString = false)
    private String company;

    @APIParam(emptyString = false)
    private String contractNum;

    @APIParam(emptyString = false)
    private String nodeUuid;

    @APIParam(emptyString = false)
    private String nodeName;

    @APIParam(emptyString = false)
    private ProductChargeModel productChargeModel;

    @APIParam(emptyString = false)
    private int duration;

    @APIParam(emptyString = false)
    private BigDecimal totalCost;

    @APIParam
    private Map<String,BigDecimal> trusteeDetails;


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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Map<String, BigDecimal> getTrusteeDetails() {
        return trusteeDetails;
    }

    public void setTrusteeDetails(Map<String, BigDecimal> trusteeDetails) {
        this.trusteeDetails = trusteeDetails;
    }

}
