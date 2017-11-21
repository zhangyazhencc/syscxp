package com.syscxp.header.tunnel.host;

import com.syscxp.header.host.HostConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;


import java.util.Set;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = HostConstant.ACTION_CATEGORY, adminOnly = true)
public class APIMonitorRunShellMsg extends APIMessage {
    @APIParam(resourceType = MonitorHostVO.class, nonempty = true)
    private Set<String> hostUuids;
    @APIParam
    private String script;

    public Set<String> getHostUuids() {
        return hostUuids;
    }

    public void setHostUuids(Set<String> hostUuids) {
        this.hostUuids = hostUuids;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
 
}
