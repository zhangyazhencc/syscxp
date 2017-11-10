package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-06
 */
@Inventory(mappingVOClass = SwitchModelVO.class)
public class SwitchModelInventory {

    private String uuid;
    private String brand;
    private String model;
    private String subModel;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static SwitchModelInventory valueOf(SwitchModelVO vo){
        SwitchModelInventory inv = new SwitchModelInventory();

        inv.setUuid(vo.getUuid());
        inv.setBrand(vo.getBrand());
        inv.setModel(vo.getModel());
        inv.setSubModel(vo.getSubModel());
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
