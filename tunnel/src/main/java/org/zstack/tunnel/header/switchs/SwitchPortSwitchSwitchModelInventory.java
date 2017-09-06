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
public class SwitchPortSwitchSwitchModelInventory {
    private String uuid;

    private String switchUuid;

    private SwitchSwitchModelInventory switchSwitchModel;

    private Integer portNum;

    private String portName;

    private SwitchPortType portType;

    private SwitchPortLabel label;

    private Integer isExclusive;

    private Integer reuse;

    private Integer enabled;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static SwitchPortSwitchSwitchModelInventory valueOf(SwitchPortVO vo){
        SwitchPortSwitchSwitchModelInventory inv = new SwitchPortSwitchSwitchModelInventory();

        inv.setUuid(vo.getUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setSwitchSwitchModel(SwitchSwitchModelInventory.valueOf(vo.getSwitchs()));
        inv.setPortNum(vo.getPortNum());
        inv.setPortName(vo.getPortName());
        inv.setPortType(vo.getPortType());
        inv.setLabel(vo.getLabel());
        inv.setIsExclusive(vo.getIsExclusive());
        inv.setReuse(vo.getReuse());
        inv.setEnabled(vo.getEnabled());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<SwitchPortSwitchSwitchModelInventory> valueOf(Collection<SwitchPortVO> vos) {
        List<SwitchPortSwitchSwitchModelInventory> lst = new ArrayList<SwitchPortSwitchSwitchModelInventory>(vos.size());
        for (SwitchPortVO vo : vos) {
            lst.add(SwitchPortSwitchSwitchModelInventory.valueOf(vo));
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

    public SwitchSwitchModelInventory getSwitchSwitchModel() {
        return switchSwitchModel;
    }

    public void setSwitchSwitchModel(SwitchSwitchModelInventory switchSwitchModel) {
        this.switchSwitchModel = switchSwitchModel;
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

    public SwitchPortLabel getLabel() {
        return label;
    }

    public void setLabel(SwitchPortLabel label) {
        this.label = label;
    }

    public Integer getReuse() {
        return reuse;
    }

    public void setReuse(Integer reuse) {
        this.reuse = reuse;
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
