package com.syscxp.header.tunnel.network;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = L3NetworkVO.class)
public class L3NetworkInventory {

    private String uuid;
    private String accountUuid;
    private String ownerAccountUuid;
    private String name;
    private String code;
    private Long vid;
    private String type;
    private String status;
    private Long endPointNum;
    private String description;
    private Long duration;
    private String productChargeModel;
    private Long maxModifies;
    private Timestamp expireDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;


    public static L3NetworkInventory valueOf(L3NetworkVO vo){
        L3NetworkInventory inv = new L3NetworkInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setOwnerAccountUuid(vo.getOwnerAccountUuid());
        inv.setName(vo.getName());
        inv.setCode(vo.getCode());
        inv.setVid(vo.getVid());
        inv.setType(vo.getType());
        inv.setStatus(vo.getStatus());
        inv.setEndPointNum(vo.getEndPointNum());
        inv.setDescription(vo.getDescription());
        inv.setDuration(vo.getDuration());
        inv.setProductChargeModel(vo.getProductChargeModel());
        inv.setMaxModifies(vo.getMaxModifies());
        inv.setExpireDate(vo.getExpireDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<L3NetworkInventory> valueOf(Collection<L3NetworkVO> vos) {
        List<L3NetworkInventory> invs = new ArrayList<L3NetworkInventory>(vos.size());
        for (L3NetworkVO vo : vos) {
            invs.add(L3NetworkInventory.valueOf(vo));
        }
        return invs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getOwnerAccountUuid() {
        return ownerAccountUuid;
    }

    public void setOwnerAccountUuid(String ownerAccountUuid) {
        this.ownerAccountUuid = ownerAccountUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getVid() {
        return vid;
    }

    public void setVid(Long vid) {
        this.vid = vid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEndPointNum() {
        return endPointNum;
    }

    public void setEndPointNum(Long endPointNum) {
        this.endPointNum = endPointNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(String productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public Long getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Long maxModifies) {
        this.maxModifies = maxModifies;
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
