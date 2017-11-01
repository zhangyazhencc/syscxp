package com.syscxp.header.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;
import java.util.Map;

/**
 * Created by DCY on 2017-09-17
 */
public class APIQueryTunnelDetailForAlarmReply extends APIQueryReply {
    private Map<String,Object> map;

    private List<TunnelDetailForAlarmInventory> inventories;

    public List<TunnelDetailForAlarmInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<TunnelDetailForAlarmInventory> inventories) {
        this.inventories = inventories;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
