package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-03.
 * @Description: 测速结果.
 */
// @Inventory(mappingVOClass = MonitorAgentCommands.SpeedResult.class)
public class SpeedResultInventory {
    private String tunnel_id;
    private boolean complete_flag;
    private float iperf_data;
    private String time_stamp;

    public static SpeedResultInventory valueOf(MonitorAgentCommands.SpeedResult vo){
        SpeedResultInventory inventory = new SpeedResultInventory();
        inventory.setTunnel_id(vo.getTunnel_id());
        inventory.setComplete_flag(vo.isComplete_flag());
        inventory.setIperf_data(vo.getIperf_data());
        inventory.setTime_stamp(vo.getTime_stamp());

        return  inventory;
    }

    public static List<SpeedResultInventory> valueOf(Collection<MonitorAgentCommands.SpeedResult> vos) {
        List<SpeedResultInventory> lst = new ArrayList<SpeedResultInventory>(vos.size());
        for (MonitorAgentCommands.SpeedResult vo : vos) {
            lst.add(SpeedResultInventory.valueOf(vo));
        }
        return lst;
    }

    public String getTunnel_id() {
        return tunnel_id;
    }

    public void setTunnel_id(String tunnel_id) {
        this.tunnel_id = tunnel_id;
    }

    public boolean isComplete_flag() {
        return complete_flag;
    }

    public void setComplete_flag(boolean complete_flag) {
        this.complete_flag = complete_flag;
    }

    public float getIperf_data() {
        return iperf_data;
    }

    public void setIperf_data(float iperf_data) {
        this.iperf_data = iperf_data;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}
