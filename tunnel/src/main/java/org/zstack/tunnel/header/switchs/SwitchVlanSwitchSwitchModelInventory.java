package org.zstack.tunnel.header.switchs;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-01
 */
@Inventory(mappingVOClass = SwitchVlanVO.class)
public class SwitchVlanSwitchSwitchModelInventory {

    private String uuid;

    private String switchUuid;

    private SwitchSwitchModelInventory switchSwitchModel;

    private Integer startVlan;

    private Integer endVlan;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static SwitchVlanSwitchSwitchModelInventory valueOf(SwitchVlanVO vo){
        SwitchVlanSwitchSwitchModelInventory inv = new SwitchVlanSwitchSwitchModelInventory();

        inv.setUuid(vo.getUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setSwitchSwitchModel(SwitchSwitchModelInventory.valueOf(vo.getSwitchs()));
        inv.setStartVlan(vo.getStartVlan());
        inv.setEndVlan(vo.getEndVlan());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<SwitchVlanSwitchSwitchModelInventory> valueOf(Collection<SwitchVlanVO> vos) {
        List<SwitchVlanSwitchSwitchModelInventory> lst = new ArrayList<SwitchVlanSwitchSwitchModelInventory>(vos.size());
        for (SwitchVlanVO vo : vos) {
            lst.add(SwitchVlanSwitchSwitchModelInventory.valueOf(vo));
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

    public Integer getStartVlan() {
        return startVlan;
    }

    public void setStartVlan(Integer startVlan) {
        this.startVlan = startVlan;
    }

    public Integer getEndVlan() {
        return endVlan;
    }

    public void setEndVlan(Integer endVlan) {
        this.endVlan = endVlan;
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
