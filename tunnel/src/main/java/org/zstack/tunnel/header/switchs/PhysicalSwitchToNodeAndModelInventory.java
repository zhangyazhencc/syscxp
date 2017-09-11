package org.zstack.tunnel.header.switchs;

import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.node.NodeInventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-06
 */
@Inventory(mappingVOClass = PhysicalSwitchVO.class)
public class PhysicalSwitchToNodeAndModelInventory {

    private String uuid;
    private String nodeUuid;
    private NodeInventory node;
    private String switchModelUuid;
    private SwitchModelInventory switchModel;
    private String code;
    private String name;
    private String brand;
    private String owner;
    private PhysicalSwitchType type;
    private String rack;
    private String description;
    private String mIP;
    private String localIP;
    private String username;
    private String password;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static PhysicalSwitchToNodeAndModelInventory valueOf(PhysicalSwitchVO vo){
        PhysicalSwitchToNodeAndModelInventory inv = new PhysicalSwitchToNodeAndModelInventory();

        inv.setUuid(vo.getUuid());
        inv.setNodeUuid(vo.getNodeUuid());
        inv.setNode(NodeInventory.valueOf(vo.getNode()));
        inv.setSwitchModelUuid(vo.getSwitchModelUuid());
        inv.setSwitchModel(SwitchModelInventory.valueOf(vo.getSwitchModel()));
        inv.setCode(vo.getCode());
        inv.setName(vo.getName());
        inv.setBrand(vo.getBrand());
        inv.setOwner(vo.getOwner());
        inv.setType(vo.getType());
        inv.setRack(vo.getRack());
        inv.setDescription(vo.getDescription());
        inv.setmIP(vo.getmIP());
        inv.setLocalIP(vo.getLocalIP());
        inv.setUsername(vo.getUsername());
        inv.setPassword(vo.getPassword());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<PhysicalSwitchToNodeAndModelInventory> valueOf(Collection<PhysicalSwitchVO> vos) {
        List<PhysicalSwitchToNodeAndModelInventory> lst = new ArrayList<PhysicalSwitchToNodeAndModelInventory>(vos.size());
        for (PhysicalSwitchVO vo : vos) {
            lst.add(PhysicalSwitchToNodeAndModelInventory.valueOf(vo));
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

    public NodeInventory getNode() {
        return node;
    }

    public void setNode(NodeInventory node) {
        this.node = node;
    }

    public String getSwitchModelUuid() {
        return switchModelUuid;
    }

    public void setSwitchModelUuid(String switchModelUuid) {
        this.switchModelUuid = switchModelUuid;
    }

    public SwitchModelInventory getSwitchModel() {
        return switchModel;
    }

    public void setSwitchModel(SwitchModelInventory switchModel) {
        this.switchModel = switchModel;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getmIP() {
        return mIP;
    }

    public void setmIP(String mIP) {
        this.mIP = mIP;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public PhysicalSwitchType getType() {
        return type;
    }

    public void setType(PhysicalSwitchType type) {
        this.type = type;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }
}
