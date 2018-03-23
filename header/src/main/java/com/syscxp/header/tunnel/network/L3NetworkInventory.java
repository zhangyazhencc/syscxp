package com.syscxp.header.tunnel.network;

import com.syscxp.header.query.ExpandedQueries;
import com.syscxp.header.query.ExpandedQuery;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = L3NetworkVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "l3Endpoint", inventoryClass = L3EndpointInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "l3NetworkUuid"),
})
public class L3NetworkInventory {

    private String uuid;
    private Long number;
    private String accountUuid;
    private String ownerAccountUuid;
    private String name;
    private String code;
    private Integer vid;
    private String type;
    private Integer endPointNum;
    private String description;
    private Integer duration;
    private String productChargeModel;
    private Integer maxModifies;
    private Timestamp expireDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;
    private boolean expired;

    public static L3NetworkInventory valueOf(L3NetworkVO vo){
        L3NetworkInventory inv = new L3NetworkInventory();
        inv.setUuid(vo.getUuid());
        inv.setNumber(vo.getNumber());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setOwnerAccountUuid(vo.getOwnerAccountUuid());
        inv.setName(vo.getName());
        inv.setCode(vo.getCode());
        inv.setVid(vo.getVid());
        inv.setType(vo.getType());
        inv.setEndPointNum(vo.getEndPointNum());
        inv.setDescription(vo.getDescription());
        inv.setDuration(vo.getDuration());
        inv.setProductChargeModel(vo.getProductChargeModel().toString());
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

    public Integer getVid() {
        return vid;
    }

    public void setVid(Integer vid) {
        this.vid = vid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getEndPointNum() {
        return endPointNum;
    }

    public void setEndPointNum(Integer endPointNum) {
        this.endPointNum = endPointNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(String productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public Integer getMaxModifies() {
        return maxModifies;
    }

    public void setMaxModifies(Integer maxModifies) {
        this.maxModifies = maxModifies;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;

        if (expireDate != null){
            if (expireDate.before(Timestamp.valueOf(LocalDateTime.now()))){
                this.expired = true;
            }
        }
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

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
