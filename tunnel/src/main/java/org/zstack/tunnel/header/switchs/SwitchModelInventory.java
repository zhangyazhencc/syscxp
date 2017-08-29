package org.zstack.tunnel.header.switchs;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-08-29
 */
@Inventory(mappingVOClass = SwitchModelVO.class)
public class SwitchModelInventory {

    private String uuid;

    private String model;

    private String subModel;

    private Integer mpls;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static SwitchModelInventory valueOf(SwitchModelVO vo){
        SwitchModelInventory inv = new SwitchModelInventory();

        inv.setUuid(vo.getUuid());
        inv.setModel(vo.getModel());
        inv.setSubModel(vo.getSubModel());
        inv.setMpls(vo.getMpls());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<SwitchModelInventory> valueOf(Collection<SwitchModelVO> vos) {
        List<SwitchModelInventory> lst = new ArrayList<SwitchModelInventory>(vos.size());
        for (SwitchModelVO vo : vos) {
            lst.add(SwitchModelInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSubModel() {
        return subModel;
    }

    public void setSubModel(String subModel) {
        this.subModel = subModel;
    }

    public Integer getMpls() {
        return mpls;
    }

    public void setMpls(Integer mpls) {
        this.mpls = mpls;
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
