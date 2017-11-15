package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.tunnel.TunnelInventory;
import com.syscxp.header.tunnel.tunnel.TunnelSwitchPortVO;

import java.sql.Timestamp;
import java.util.*;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: .
 */
@Inventory(mappingVOClass = SpeedTestTunnelVO.class)
public class SpeedTestTunnelInventory {
    private String uuid;
    private String tunnelUuid;
    private TunnelInventory tunnelInventory;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SpeedTestTunnelInventory valueOf(SpeedTestTunnelVO vo){
        SpeedTestTunnelInventory inventory = new SpeedTestTunnelInventory();
        inventory.setUuid(vo.getUuid());
        inventory.setTunnelUuid(vo.getTunnelUuid());
        inventory.setTunnelInventory(TunnelInventory.valueOf(vo.getTunnelVO()));
        inventory.setLastOpDate(vo.getLastOpDate());
        inventory.setCreateDate(vo.getCreateDate());

        return  inventory;
    }

    public static List<SpeedTestTunnelInventory> valueOf(Collection<SpeedTestTunnelVO> vos) {
        List<SpeedTestTunnelInventory> lst = new ArrayList<SpeedTestTunnelInventory>(vos.size());
        for (SpeedTestTunnelVO vo : vos) {
            lst.add(SpeedTestTunnelInventory.valueOf(vo));
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

    public TunnelInventory getTunnelInventory() {
        return tunnelInventory;
    }

    public void setTunnelInventory(TunnelInventory tunnelInventory) {
        this.tunnelInventory = tunnelInventory;
    }
}
