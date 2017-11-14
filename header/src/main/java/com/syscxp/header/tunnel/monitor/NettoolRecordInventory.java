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
public class NettoolRecordInventory {
    private String command;
    private String remoteIp;
    private String guid;
    private String hostIp;

    public static NettoolRecordInventory valueOf(MonitorAgentCommands.NettoolCommand vo,String hostIp){
        NettoolRecordInventory inventory = new NettoolRecordInventory();
        inventory.setCommand(vo.getCommand());
        inventory.setRemoteIp(vo.getRemote_ip());
        inventory.setGuid(vo.getGuid());
        inventory.setHostIp(hostIp);

        return  inventory;
    }

    public static List<NettoolRecordInventory> valueOf(Collection<MonitorAgentCommands.NettoolCommand> vos,String hostIp) {
        List<NettoolRecordInventory> lst = new ArrayList<NettoolRecordInventory>(vos.size());
        for (MonitorAgentCommands.NettoolCommand vo : vos) {
            lst.add(NettoolRecordInventory.valueOf(vo,hostIp));
        }
        return lst;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
}
