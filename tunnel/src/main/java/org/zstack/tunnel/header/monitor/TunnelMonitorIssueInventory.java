package org.zstack.tunnel.header.monitor;

import org.zstack.header.search.Inventory;
import org.zstack.utils.gson.JSONObjectUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-26.
 * @Description: RYU 控制器监控下发.
 */
//@Inventory(mappingVOClass = TunnelMonitorVO.class)
public class TunnelMonitorIssueInventory {

    private String tunnel_uuid;
    private  List<TunnelMonitorIssueDetailInventory> detailInventories;

    public String getTunnel_uuid() {
        return tunnel_uuid;
    }

    public void setTunnel_uuid(String tunnel_uuid) {
        this.tunnel_uuid = tunnel_uuid;
    }

    public List<TunnelMonitorIssueDetailInventory> getDetailInventories() {
        return detailInventories;
    }

    public void setDetailInventories(List<TunnelMonitorIssueDetailInventory> detailInventories) {
        this.detailInventories = detailInventories;
    }
}

