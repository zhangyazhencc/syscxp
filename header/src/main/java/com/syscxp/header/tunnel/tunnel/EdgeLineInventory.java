package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2018/1/9
 */
@Inventory(mappingVOClass = EdgeLineVO.class)
public class EdgeLineInventory {

    private String uuid;
    private String accountUuid;
    private String interfaceUuid;
    private String interfaceName;
    private String type;
    private String destinationInfo;
    private String endpointUuid;
    private String endpointName;
    private String description;
    private EdgeLineState state;
    private Integer prices;
    private Timestamp expireDate;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static EdgeLineInventory valueOf(EdgeLineVO vo){
        EdgeLineInventory inv = new EdgeLineInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setInterfaceUuid(vo.getInterfaceUuid());
        inv.setInterfaceName(vo.getInterfaceVO().getName());
        inv.setType(vo.getType());
        inv.setDestinationInfo(vo.getDestinationInfo());
        inv.setEndpointUuid(vo.getInterfaceVO().getEndpointUuid());
        inv.setEndpointName(vo.getInterfaceVO().getEndpointVO().getName());
        inv.setDescription(vo.getDescription());
        inv.setState(vo.getState());
        inv.setPrices(vo.getPrices());
        inv.setExpireDate(vo.getExpireDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<EdgeLineInventory> valueOf(Collection<EdgeLineVO> vos) {
        List<EdgeLineInventory> lst = new ArrayList<EdgeLineInventory>(vos.size());
        for (EdgeLineVO vo : vos) {
            lst.add(EdgeLineInventory.valueOf(vo));
        }
        return lst;
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

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EdgeLineState getState() {
        return state;
    }

    public void setState(EdgeLineState state) {
        this.state = state;
    }

    public Integer getPrices() {
        return prices;
    }

    public void setPrices(Integer prices) {
        this.prices = prices;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
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

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestinationInfo() {
        return destinationInfo;
    }

    public void setDestinationInfo(String destinationInfo) {
        this.destinationInfo = destinationInfo;
    }
}
