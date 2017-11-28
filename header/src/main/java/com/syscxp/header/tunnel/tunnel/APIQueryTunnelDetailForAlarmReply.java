package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.falconapi.FalconApiCommands;
import com.syscxp.header.query.APIQueryReply;

import java.util.List;
import java.util.Map;

/**
 * Created by DCY on 2017-09-17
 */
public class APIQueryTunnelDetailForAlarmReply extends APIQueryReply {
    private List<FalconApiCommands.FalconTunnelInventory> inventories ;

    public List<FalconApiCommands.FalconTunnelInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<FalconApiCommands.FalconTunnelInventory> inventories) {
        this.inventories = inventories;
    }
}
