package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2017/10/11
 */
@Inventory(mappingVOClass = QinqVO.class)
public class QinqInventory {

    private String uuid;

    private String tunnelUuid;

    private Integer startVlan;

    private Integer endVlan;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static QinqInventory valueOf(QinqVO vo){
        QinqInventory inv = new QinqInventory();

        inv.setUuid(vo.getUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setStartVlan(vo.getStartVlan());
        inv.setEndVlan(vo.getEndVlan());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<QinqInventory> valueOf(Collection<QinqVO> vos) {
        List<QinqInventory> lst = new ArrayList<QinqInventory>(vos.size());
        for (QinqVO vo : vos) {
            lst.add(QinqInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
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
