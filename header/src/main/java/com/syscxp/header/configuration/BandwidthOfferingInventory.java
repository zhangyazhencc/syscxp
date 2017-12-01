package com.syscxp.header.configuration;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/10/30
 */
@Inventory(mappingVOClass = BandwidthOfferingVO.class)
public class BandwidthOfferingInventory {

    private String uuid;
    private String name;
    private String description;
    private Long bandwidth;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static BandwidthOfferingInventory valueOf(BandwidthOfferingVO vo){
        BandwidthOfferingInventory inv = new BandwidthOfferingInventory();

        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setBandwidth(vo.getBandwidth());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<BandwidthOfferingInventory> valueOf(Collection<BandwidthOfferingVO> vos) {
        List<BandwidthOfferingInventory> lst = new ArrayList<BandwidthOfferingInventory>(vos.size());
        for (BandwidthOfferingVO vo : vos) {
            lst.add(BandwidthOfferingInventory.valueOf(vo));
        }
        return lst;
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

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
