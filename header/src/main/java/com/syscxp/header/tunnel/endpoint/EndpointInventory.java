package com.syscxp.header.tunnel.endpoint;

/**
 * Created by DCY on 2017-08-23
 */

import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.node.NodeInventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = EndpointVO.class)
public class EndpointInventory {
    private String uuid;
    private String nodeUuid;
    private NodeInventory node;
    private String name;
    private String code;
    private String endpointType;
    private String cloudType;
    private String state;
    private String status;
    private String description;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static EndpointInventory valueOf(EndpointVO vo){
        EndpointInventory inv = new EndpointInventory();
        inv.setUuid(vo.getUuid());
        inv.setNodeUuid(vo.getNodeUuid());
        inv.setNode(NodeInventory.valueOf(vo.getNodeVO()));
        inv.setName(vo.getName());
        inv.setCode(vo.getCode());
        inv.setEndpointType(vo.getEndpointType().toString());
        inv.setCloudType(vo.getCloudType());
        inv.setState(vo.getState().toString());
        inv.setStatus(vo.getStatus().toString());
        inv.setDescription(vo.getDescription());
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

    public String getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(String endpointType) {
        this.endpointType = endpointType;
    }

    public NodeInventory getNode() {
        return node;
    }

    public void setNode(NodeInventory node) {
        this.node = node;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCloudType() {
        return cloudType;
    }

    public void setCloudType(String cloudType) {
        this.cloudType = cloudType;
    }
}
