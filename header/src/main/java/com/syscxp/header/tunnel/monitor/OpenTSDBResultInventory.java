package com.syscxp.header.tunnel.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: OpenTSDB返回集合.
 */
public class OpenTSDBResultInventory {
    private String nodeUuid;
    private String metric;
    private OpenTSDBCommands.Tags tags;
    private List aggregateTags;
    private Map dps;

    public static OpenTSDBResultInventory valueOf(OpenTSDBCommands.QueryResult vo){
        OpenTSDBResultInventory inventory = new OpenTSDBResultInventory();
        inventory.setMetric(vo.getMetric());
        inventory.setTags(vo.getTags());
        inventory.setAggregateTags(vo.getAggregateTags());
        inventory.setDps(vo.getDps());
        inventory.setNodeUuid(vo.getNodeUuid());

        return  inventory;
    }

    public static List<OpenTSDBResultInventory> valueOf(Collection<OpenTSDBCommands.QueryResult> vos) {
        List<OpenTSDBResultInventory> lst = new ArrayList<OpenTSDBResultInventory>(vos.size());
        for (OpenTSDBCommands.QueryResult vo : vos) {
            lst.add(OpenTSDBResultInventory.valueOf(vo));
        }
        return lst;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public OpenTSDBCommands.Tags getTags() {
        return tags;
    }

    public void setTags(OpenTSDBCommands.Tags tags) {
        this.tags = tags;
    }

    public List getAggregateTags() {
        return aggregateTags;
    }

    public void setAggregateTags(List aggregateTags) {
        this.aggregateTags = aggregateTags;
    }


    public Map getDps() {
        return dps;
    }

    public void setDps(Map dps) {
        this.dps = dps;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }
}
