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

    private SwitchPortLabel label;

    private Integer reuse;

    private Integer autoAlloc;

    private Integer enabled;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static SwitchPortInventory valueOf(SwitchPortVO vo){
        SwitchPortInventory inv = new SwitchPortInventory();

        inv.setUuid(vo.getUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setPortNum(vo.getPortNum());
        inv.setPortName(vo.getPortName());
        inv.setLabel(vo.getLabel());
        inv.setReuse(vo.getReuse());
        inv.setAutoAlloc(vo.getAutoAlloc());
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

    public Integer getAutoAlloc() {
        return autoAlloc;
    }

    public void setAutoAlloc(Integer autoAlloc) {
        this.autoAlloc = autoAlloc;
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
}
