package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.search.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-03.
 * @Description: 网络工具返回集合.
 */
public class NettoolResultInventory {
    private String guid;
    private String result;

    public static NettoolResultInventory valueOf(MonitorAgentCommands.NettoolResult vo){
        NettoolResultInventory inventory = new NettoolResultInventory();
        inventory.setResult(vo.getResult());
        inventory.setGuid(vo.getGuid());

        return  inventory;
    }

    public static List<NettoolResultInventory> valueOf(Collection<MonitorAgentCommands.NettoolResult> vos) {
        List<NettoolResultInventory> lst = new ArrayList<NettoolResultInventory>(vos.size());
        for (MonitorAgentCommands.NettoolResult vo : vos) {
            lst.add(NettoolResultInventory.valueOf(vo));
        }
        return lst;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
