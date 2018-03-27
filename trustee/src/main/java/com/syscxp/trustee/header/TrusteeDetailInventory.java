package com.syscxp.trustee.header;

import com.syscxp.header.search.Inventory;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = TrustDetailVO.class)
public class TrusteeDetailInventory {

    private String uuid;
    private String name;
    private String description;
    private String trusteeUuid;
    private BigDecimal cost;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static TrusteeDetailInventory valueOf(TrustDetailVO vo){
        TrusteeDetailInventory inv = new TrusteeDetailInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setCost(vo.getCost());
        inv.setTrusteeUuid(vo.getTrusteeUuid());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<TrusteeDetailInventory> valueOf(Collection<TrustDetailVO> vos) {
        List<TrusteeDetailInventory> invs = new ArrayList<>(vos.size());
        for (TrustDetailVO vo : vos) {
            invs.add(TrusteeDetailInventory.valueOf(vo));
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
