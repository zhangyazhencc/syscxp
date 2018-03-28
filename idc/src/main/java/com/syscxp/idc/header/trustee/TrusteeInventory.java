package com.syscxp.idc.header.trustee;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = TrusteeVO.class)
public class TrusteeInventory {

    private String uuid;
    private String name;
    private String description;
    private String accountName;
    private String accountUuid;
    private String company;
    private String contractNum;
    private String nodeUuid;
    private String nodeName;
    private ProductChargeModel productChargeModel;
    private BigDecimal totalCost;
    private Timestamp expireDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TrusteeInventory valueOf(TrusteeVO vo){
        TrusteeInventory inv = new TrusteeInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setAccountName(vo.getAccountName());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setCompany(vo.getCompany());
        inv.setNodeName(vo.getNodeName());
        inv.setNodeUuid(vo.getNodeUuid());
        inv.setContractNum(vo.getContractNum());
        inv.setProductChargeModel(vo.getProductChargeModel());
        inv.setDescription(vo.getDescription());
        inv.setTotalCost(vo.getTotalCost());
        inv.setExpireDate(vo.getExpireDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TrusteeInventory> valueOf(Collection<TrusteeVO> vos) {
        List<TrusteeInventory> invs = new ArrayList<>(vos.size());
        for (TrusteeVO vo : vos) {
            invs.add(TrusteeInventory.valueOf(vo));
        }
        return invs;
    }
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
