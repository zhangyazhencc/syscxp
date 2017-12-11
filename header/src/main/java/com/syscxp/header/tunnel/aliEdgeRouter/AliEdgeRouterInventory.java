package com.syscxp.header.tunnel.aliEdgeRouter;

import java.sql.Timestamp;

import com.syscxp.header.query.ExpandedQueries;
import com.syscxp.header.query.ExpandedQuery;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.tunnel.TunnelInventory;

import java.util.*;

@Inventory(mappingVOClass = AliEdgeRouterVO.class)
@ExpandedQueries({
        @ExpandedQuery(expandedField = "tunnel", inventoryClass = TunnelInventory.class,
                foreignKey = "tunnelUuid", expandedInventoryKey = "uuid"),
})
public class AliEdgeRouterInventory {
    private String uuid;
    private String tunnelUuid;
    private String accountUuid;
    private String aliAccountUuid;
    private String aliRegionId;
    private String name;
    private String description;
    private String vbrUuid;
    private String physicalLineUuid;
    private Integer vlan;
    private Timestamp lastOpDate;
    private Timestamp createDate;
    private boolean isCreateFlag;

    private String tunnelName;


    public static AliEdgeRouterInventory valueOf(AliEdgeRouterVO vo){
        AliEdgeRouterInventory inv = new AliEdgeRouterInventory();
        inv.setUuid(vo.getUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setAliAccountUuid(vo.getAliAccountUuid());
        inv.setAliRegionId(vo.getAliRegionId());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setVbrUuid(vo.getVbrUuid());
        inv.setPhysicalLineUuid(vo.getPhysicalLineUuid());
        inv.setVlan(vo.getVlan());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        inv.setCreateFlag(vo.isCreateFlag());
        if(vo.getTunnelEO() != null){
            inv.setTunnelName(vo.getTunnelEO().getName());
        }

        return inv;
    }

    public static List<AliEdgeRouterInventory> valueOf(Collection<AliEdgeRouterVO> vos) {
        List<AliEdgeRouterInventory> lst = new ArrayList<AliEdgeRouterInventory>(vos.size());
        for (AliEdgeRouterVO vo : vos) {
            lst.add(AliEdgeRouterInventory.valueOf(vo));
        }
        return lst;
    }



    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getAliAccountUuid() {
        return aliAccountUuid;
    }

    public void setAliAccountUuid(String aliAccountUuid) {
        this.aliAccountUuid = aliAccountUuid;
    }

    public String getAliRegionId() {
        return aliRegionId;
    }

    public void setAliRegionId(String aliRegionId) {
        this.aliRegionId = aliRegionId;
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

    public String getVbrUuid() {
        return vbrUuid;
    }

    public void setVbrUuid(String vbrUuid) {
        this.vbrUuid = vbrUuid;
    }

    public String getPhysicalLineUuid() {
        return physicalLineUuid;
    }

    public void setPhysicalLineUuid(String physicalLineUuid) {
        this.physicalLineUuid = physicalLineUuid;
    }

    public Integer getVlan() {
        return vlan;
    }

    public void setVlan(Integer vlan) {
        this.vlan = vlan;
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

    public boolean isCreateFlag() {
        return isCreateFlag;
    }

    public void setCreateFlag(boolean createFlag) {
        isCreateFlag = createFlag;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }
}
