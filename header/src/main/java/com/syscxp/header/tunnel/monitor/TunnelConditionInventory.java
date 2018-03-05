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
    private Map<String,OpenTSDBCommands.Tags> tags;

    public static TunnelConditionInventory valueOf(OpenTSDBCommands.TunnelCondition vo){
        TunnelConditionInventory inventory = new TunnelConditionInventory();
        inventory.setNodeUuid(vo.getNodeUuid());
        inventory.setTags(vo.getTags());

        return  inventory;
    }

    public static List<TunnelConditionInventory> valueOf(Collection<OpenTSDBCommands.TunnelCondition> vos) {
        List<TunnelConditionInventory> lst = new ArrayList<TunnelConditionInventory>(vos.size());
        for (OpenTSDBCommands.TunnelCondition vo : vos) {
            lst.add(TunnelConditionInventory.valueOf(vo));
        }
        return lst;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public Map<String, OpenTSDBCommands.Tags> getTags() {
        return tags;
    }

    public void setTags(Map<String, OpenTSDBCommands.Tags> tags) {
        this.tags = tags;
    }
}
