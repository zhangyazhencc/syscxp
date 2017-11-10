package com.syscxp.tunnel.host;

import com.syscxp.header.core.workflow.Flow;

public interface MonitorHostConnectExtensionPoint {
    Flow createHostConnectingFlow(MonitorHostConnectedContext context);
}
