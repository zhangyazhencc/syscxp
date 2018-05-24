package com.syscxp.header.tunnel.cloudhub;


import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.tunnel.TunnelType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = CloudHubOfferingVO.class)
public class CloudHubOfferingInventory {

    private String uuid;
    private String name;
    private String area;
    private Integer number;
    private Long bandwidth;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static CloudHubOfferingInventory valueOf(CloudHubOfferingVO vo) {
        CloudHubOfferingInventory inv = new CloudHubOfferingInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setArea(vo.getArea().toString());
        inv.setNumber(vo.getNumber());
        inv.setBandwidth(vo.getBandwidth());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<CloudHubOfferingInventory> valueOf(Collection<CloudHubOfferingVO> vos) {
        List<CloudHubOfferingInventory> lst = new ArrayList<CloudHubOfferingInventory>(vos.size());
        for (CloudHubOfferingVO vo : vos) {
            lst.add(CloudHubOfferingInventory.valueOf(vo));
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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
