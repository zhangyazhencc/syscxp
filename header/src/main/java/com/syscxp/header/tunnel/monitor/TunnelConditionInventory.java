package com.syscxp.header.tunnel.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-05.
 * @Description: 获取专线查询OpenTSDB查询条件.
 */
public class TunnelConditionInventory {
    private String nodeUuid;
    private String endpointUuid;
    private Map<String,OpenTSDBCommands.Tags> tags;

    public static TunnelConditionInventory valueOf(OpenTSDBCommands.CustomCondition vo){
        TunnelConditionInventory inventory = new TunnelConditionInventory();
        inventory.setNodeUuid(vo.getNodeUuid());
        inventory.setEndpointUuid(vo.getEndpointUuid());
        inventory.setTags(vo.getTags());

        return  inventory;
    }

    public static List<TunnelConditionInventory> valueOf(Collection<OpenTSDBCommands.CustomCondition> vos) {
        List<TunnelConditionInventory> lst = new ArrayList<TunnelConditionInventory>(vos.size());
        for (OpenTSDBCommands.CustomCondition vo : vos) {
            lst.add(TunnelConditionInventory.valueOf(vo));
        }
        return lst;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public Map<String, OpenTSDBCommands.Tags> getTags() {
        return tags;
    }

    public void setTags(Map<String, OpenTSDBCommands.Tags> tags) {
        this.tags = tags;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }
}
