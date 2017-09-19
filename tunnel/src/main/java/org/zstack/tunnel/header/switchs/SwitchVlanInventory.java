package org.zstack.tunnel.header.switchs;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-08-29
 */
@Inventory(mappingVOClass = SwitchVlanVO.class)
public class SwitchVlanInventory {

    private String uuid;

    private String switchUuid;

    private SwitchInventory switchs;

    private Integer startVlan;

    private Integer endVlan;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static SwitchVlanInventory valueOf(SwitchVlanVO vo){
        SwitchVlanInventory inv = new SwitchVlanInventory();

        inv.setUuid(vo.getUuid());
        inv.setSwitchUuid(vo.getSwitchUuid());
        inv.setSwitchs(SwitchInventory.valueOf(vo.getSwitchs()));
        inv.setStartVlan(vo.getStartVlan());
        inv.setEndVlan(vo.getEndVlan());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<SwitchVlanInventory> valueOf(Collection<SwitchVlanVO> vos) {
        List<SwitchVlanInventory> lst = new ArrayList<SwitchVlanInventory>(vos.size());
        for (SwitchVlanVO vo : vos) {
            lst.add(SwitchVlanInventory.valueOf(vo));
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

    public SwitchInventory getSwitchs() {
        return switchs;
    }

    public void setSwitchs(SwitchInventory switchs) {
        this.switchs = switchs;
    }
}
