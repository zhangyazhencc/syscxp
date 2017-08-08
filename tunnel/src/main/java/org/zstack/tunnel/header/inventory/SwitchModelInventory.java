package org.zstack.tunnel.header.inventory;

import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.SwitchModelVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = SwitchModelVO.class)
public class SwitchModelInventory {

    private String uuid;
    private String model;
    private String subModel;
    private String mpls;
    private Timestamp lastOpDate;
    private Timestamp createDate;


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

    public String getMpls() {
        return mpls;
    }

    public void setMpls(String mpls) {
        this.mpls = mpls;
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
}
