package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-08-29
 */
@Inventory(mappingVOClass = SwitchPortVO.class)
public class SwitchPortInventory {
    private String uuid;

    private String switchUuid;

    private SwitchInventory switchs;

    private Integer portNum;

    private String portName;

    private String portType;

    private String portAttribute;

    private Integer autoAllot;

    private String state;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static SwitchPortInventory valueOf(SwitchPortVO vo){
        SwitchPortInventory inv = new SwitchPortInventory();

        inv.setUuid(vo.getUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setSwitchs(SwitchInventory.valueOf(vo.getSwitchs()));
        inv.setPortNum(vo.getPortNum());
        inv.setPortName(vo.getPortName());
        inv.setPortType(vo.getPortType());
        inv.setPortAttribute(vo.getPortAttribute());
        inv.setAutoAllot(vo.getAutoAllot());
        inv.setState(vo.getState().toString());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<SwitchPortInventory> valueOf(Collection<SwitchPortVO> vos) {
        List<SwitchPortInventory> lst = new ArrayList<SwitchPortInventory>(vos.size());
        for (SwitchPortVO vo : vos) {
            lst.add(SwitchPortInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSwitchUuid() {
        return switchUuid;
    }

    public void setSwitchUuid(String switchUuid) {
        this.switchUuid = switchUuid;
    }

    public Integer getPortNum() {
        return portNum;
    }

    public void setPortNum(Integer portNum) {
        this.portNum = portNum;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
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

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public SwitchInventory getSwitchs() {
        return switchs;
    }

    public void setSwitchs(SwitchInventory switchs) {
        this.switchs = switchs;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getAutoAllot() {
        return autoAllot;
    }

    public void setAutoAllot(Integer autoAllot) {
        this.autoAllot = autoAllot;
    }

    public String getPortAttribute() {
        return portAttribute;
    }

    public void setPortAttribute(String portAttribute) {
        this.portAttribute = portAttribute;
    }
}
