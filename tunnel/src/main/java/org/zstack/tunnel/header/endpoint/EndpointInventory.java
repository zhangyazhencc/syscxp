package org.zstack.tunnel.header.endpoint;

/**
 * Created by DCY on 2017-08-23
 */

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = EndpointVO.class)
public class EndpointInventory {
    private String uuid;
    private String nodeUuid;
    private String name;
    private String code;
    private Integer enabled;
    private Integer openToCustomers;
    private String description;
    private EndpointStatus status;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static EndpointInventory valueOf(EndpointVO vo){
        EndpointInventory inv = new EndpointInventory();
        inv.setUuid(vo.getUuid());
        inv.setNodeUuid(vo.getNodeUuid());
        inv.setName(vo.getName());
        inv.setCode(vo.getCode());
        inv.setEnabled(vo.getEnabled());
        inv.setOpenToCustomers(vo.getOpenToCustomers());
        inv.setDescription(vo.getDescription());
        inv.setStatus(vo.getStatus());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<EndpointInventory> valueOf(Collection<EndpointVO> vos) {
        List<EndpointInventory> lst = new ArrayList<EndpointInventory>(vos.size());
        for (EndpointVO vo : vos) {
            lst.add(EndpointInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
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

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getOpenToCustomers() {
        return openToCustomers;
    }

    public void setOpenToCustomers(Integer openToCustomers) {
        this.openToCustomers = openToCustomers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public EndpointStatus getStatus() {
        return status;
    }

    public void setStatus(EndpointStatus status) {
        this.status = status;
    }
}
