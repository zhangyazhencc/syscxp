package com.syscxp.vpn.host;

import com.syscxp.header.core.workflow.Flow;

public interface VpnHostConnectExtensionPoint {
    Flow createHostConnectingFlow(VpnHostConnectedContext context);
}
