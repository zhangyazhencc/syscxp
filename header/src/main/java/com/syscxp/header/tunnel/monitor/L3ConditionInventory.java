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
public class L3ConditionInventory {
    private String l3EndpointUuid;
    private OpenTSDBCommands.L3Tags trafficTags;
    private Map<String, OpenTSDBCommands.L3Tags> icmpTags;

    public static L3ConditionInventory valueOf(OpenTSDBCommands.L3CustomCondition vo){
        L3ConditionInventory inventory = new L3ConditionInventory();
        inventory.setL3EndpointUuid(vo.getL3EndpointUuid());
        inventory.setTrafficTags(vo.getTrafficTags());
        inventory.setIcmpTags(vo.getIcmpTags());

        return  inventory;
    }

    public static List<L3ConditionInventory> valueOf(Collection<OpenTSDBCommands.L3CustomCondition> vos) {
        List<L3ConditionInventory> lst = new ArrayList<L3ConditionInventory>(vos.size());
        for (OpenTSDBCommands.L3CustomCondition vo : vos) {
            lst.add(L3ConditionInventory.valueOf(vo));
        }
        return lst;
    }

    public String getL3EndpointUuid() {
        return l3EndpointUuid;
    }

    public void setL3EndpointUuid(String l3EndpointUuid) {
        this.l3EndpointUuid = l3EndpointUuid;
    }

    public OpenTSDBCommands.L3Tags getTrafficTags() {
        return trafficTags;
    }

    public void setTrafficTags(OpenTSDBCommands.L3Tags trafficTags) {
        this.trafficTags = trafficTags;
    }

    public Map<String, OpenTSDBCommands.L3Tags> getIcmpTags() {
        return icmpTags;
    }

    public void setIcmpTags(Map<String, OpenTSDBCommands.L3Tags> icmpTags) {
        this.icmpTags = icmpTags;
    }
}
