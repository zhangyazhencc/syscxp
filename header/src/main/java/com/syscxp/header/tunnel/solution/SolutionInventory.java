package com.syscxp.header.tunnel.solution;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = SolutionVO.class)
public class SolutionInventory {
    private String uuid;
    private String name;
    private String accountUuid;
    private String description;
    private String totalCost;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SolutionInventory valueOf(SolutionVO vo) {
        SolutionInventory inv = new SolutionInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setTotalCost(vo.getTotalCost());
        inv.setDescription(vo.getDescription());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setAccountUuid(vo.getAccountUuid());
        return inv;
    }

    public static List<SolutionInventory> valueOf(Collection<SolutionVO> vos) {
        List<SolutionInventory> list = new ArrayList<>(vos.size());
        for (SolutionVO vo : vos) {
            list.add(SolutionInventory.valueOf(vo));
        }
        return list;
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

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
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
