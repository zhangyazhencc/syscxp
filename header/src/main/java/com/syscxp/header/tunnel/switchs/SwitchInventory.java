package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.endpoint.EndpointInventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-08-24
 */
@Inventory(mappingVOClass = SwitchVO.class)
public class SwitchInventory {

    private String uuid;
    private String endpointUuid;
    private EndpointInventory endpoint;
    private String code;
    private String name;
    private String type;
    private String physicalSwitchUuid;
    private PhysicalSwitchInventory physicalSwitch;
    private String description;
    private String state;
    private String status;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SwitchInventory valueOf(SwitchVO vo){
        SwitchInventory inv = new SwitchInventory();

        inv.setUuid(vo.getUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setEndpoint(EndpointInventory.valueOf(vo.getEndpoint()));
        inv.setCode(vo.getCode());
        inv.setName(vo.getName());
        inv.setType(vo.getType().toString());
        inv.setPhysicalSwitchUuid(vo.getPhysicalSwitchUuid());
        inv.setPhysicalSwitch(PhysicalSwitchInventory.valueOf(vo.getPhysicalSwitch()));
        inv.setDescription(vo.getDescription());
        inv.setState(vo.getState().toString());
        inv.setStatus(vo.getStatus().toString());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<SwitchInventory> valueOf(Collection<SwitchVO> vos) {
        List<SwitchInventory> lst = new ArrayList<SwitchInventory>(vos.size());
        for (SwitchVO vo : vos) {
            lst.add(SwitchInventory.valueOf(vo));
        }
        return lst;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhysicalSwitchUuid() {
        return physicalSwitchUuid;
    }

    public void setPhysicalSwitchUuid(String physicalSwitchUuid) {
        this.physicalSwitchUuid = physicalSwitchUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public EndpointInventory getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointInventory endpoint) {
        this.endpoint = endpoint;
    }

    public PhysicalSwitchInventory getPhysicalSwitch() {
        return physicalSwitch;
    }

    public void setPhysicalSwitch(PhysicalSwitchInventory physicalSwitch) {
        this.physicalSwitch = physicalSwitch;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
