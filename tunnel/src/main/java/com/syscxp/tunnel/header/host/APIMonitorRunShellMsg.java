package com.syscxp.tunnel.header.host;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;


import java.util.Set;


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
