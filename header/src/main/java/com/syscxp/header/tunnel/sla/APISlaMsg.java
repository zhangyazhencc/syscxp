package com.syscxp.header.tunnel.sla;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.host.MonitorHostVO;
import com.syscxp.header.tunnel.monitor.OpenTSDBCommands;

import java.util.List;


@Action(services = {TunnelConstant.ACTION_SERVICE}, category = MonitorConstant.ACTION_CATEGORY, names = {"create"},  adminOnly = true)
public class APISlaMsg extends APIMessage {

    @APIParam
    private List<OpenTSDBCommands.QueryResult> monitorDatas;

    public List<OpenTSDBCommands.QueryResult> getMonitorDatas() {
        return monitorDatas;
    }

    public void setMonitorDatas(List<OpenTSDBCommands.QueryResult> monitorDatas) {
        this.monitorDatas = monitorDatas;
    }


}
