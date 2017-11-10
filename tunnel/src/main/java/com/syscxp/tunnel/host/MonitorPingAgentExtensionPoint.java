package com.syscxp.tunnel.host;

import com.syscxp.header.core.Completion;
import com.syscxp.header.tunnel.host.MonitorHostInventory;

public interface MonitorPingAgentExtensionPoint {
    void monitorPingAgent(MonitorHostInventory host, Completion completion);
}
