package org.zstack.tunnel.header.switchs;

import org.zstack.header.search.Inventory;

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

    private Integer portNum;

    private String portName;

    private SwitchPortType portType;

    private Integer isExclusive;

    private Integer enabled;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static SwitchPortInventory valueOf(SwitchPortVO vo){
        SwitchPortInventory inv = new SwitchPortInventory();

        inv.setUuid(vo.getUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setPortNum(vo.getPortNum());
        inv.setPortName(vo.getPortName());
        inv.setPortType(vo.getPortType());
        inv.setIsExclusive(vo.getIsExclusive());
        inv.setEnabled(vo.getEnabled());
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
