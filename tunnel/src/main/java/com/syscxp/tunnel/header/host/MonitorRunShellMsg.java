package com.syscxp.tunnel.header.host;

import com.syscxp.header.host.HostMessage;
import com.syscxp.header.message.NeedReplyMessage;

/**
 * Created by xing5 on 2016/3/14.
 */
public class MonitorRunShellMsg extends NeedReplyMessage implements HostMessage {
    private String hostUuid;
    private String script;

    @Override
    public String getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(String hostUuid) {
        this.hostUuid = hostUuid;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
