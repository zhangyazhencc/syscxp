package com.syscxp.tunnel.header.host;

import com.syscxp.header.core.workflow.Flow;

public interface MonitorHostConnectExtensionPoint {
    Flow createHostConnectingFlow(MonitorHostConnectedContext context);
}
