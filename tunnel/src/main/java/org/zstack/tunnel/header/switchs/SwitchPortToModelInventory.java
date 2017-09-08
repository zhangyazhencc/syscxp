package org.zstack.tunnel.header.switchs;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-01
 */
@Inventory(mappingVOClass = SwitchPortVO.class)
public class SwitchPortToModelInventory {
    private String uuid;

    private String switchUuid;

    private SwitchToModelInventory switchs;

    private Integer portNum;

    private String portName;

    private SwitchPortType portType;

    private Integer isExclusive;

    private Integer enabled;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static SwitchPortToModelInventory valueOf(SwitchPortVO vo){
        SwitchPortToModelInventory inv = new SwitchPortToModelInventory();

        inv.setUuid(vo.getUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setSwitchs(SwitchToModelInventory.valueOf(vo.getSwitchs()));
        inv.setPortNum(vo.getPortNum());
        inv.setPortName(vo.getPortName());
        inv.setPortType(vo.getPortType());
        inv.setIsExclusive(vo.getIsExclusive());
        inv.setEnabled(vo.getEnabled());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<SwitchPortToModelInventory> valueOf(Collection<SwitchPortVO> vos) {
        List<SwitchPortToModelInventory> lst = new ArrayList<SwitchPortToModelInventory>(vos.size());
        for (SwitchPortVO vo : vos) {
            lst.add(SwitchPortToModelInventory.valueOf(vo));
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

    public SwitchToModelInventory getSwitchs() {
        return switchs;
    }

    public void setSwitchs(SwitchToModelInventory switchs) {
        this.switchs = switchs;
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

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
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

    public SwitchPortType getPortType() {
        return portType;
    }

    public void setPortType(SwitchPortType portType) {
        this.portType = portType;
    }

    public Integer getIsExclusive() {
        return isExclusive;
    }

    public void setIsExclusive(Integer isExclusive) {
        this.isExclusive = isExclusive;
    }
}
