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
    private String metric;
    private OpenTSDBCommands.tags tags;
    private List agggregateTags;
    private List<Map<Long,Object>> dps;

    public static OpenTSDBResultInventory valueOf(OpenTSDBCommands.QueryResult vo){
        OpenTSDBResultInventory inventory = new OpenTSDBResultInventory();
        inventory.setMetric(vo.getMetric());
        inventory.setTags(vo.getTags());
        inventory.setAgggregateTags(vo.getAgggregateTags());
        inventory.setDps(vo.getDps());

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

    public OpenTSDBCommands.tags getTags() {
        return tags;
    }

    public void setTags(OpenTSDBCommands.tags tags) {
        this.tags = tags;
    }

    public List getAgggregateTags() {
        return agggregateTags;
    }

    public void setAgggregateTags(List agggregateTags) {
        this.agggregateTags = agggregateTags;
    }

    public List<Map<Long, Object>> getDps() {
        return dps;
    }

    public void setDps(List<Map<Long, Object>> dps) {
        this.dps = dps;
    }
}
