package com.syscxp.tunnel.host;

import com.syscxp.header.core.Completion;

public interface MonitorPingAgentExtensionPoint {
    void monitorPingAgent(MonitorHostInventory host, Completion completion);
}
