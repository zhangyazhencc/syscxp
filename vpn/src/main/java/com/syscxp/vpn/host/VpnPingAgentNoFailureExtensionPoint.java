package com.syscxp.vpn.host;

import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.vpn.host.VpnHostInventory;

public interface VpnPingAgentNoFailureExtensionPoint {
    void vpnPingAgentNoFailure(VpnHostInventory host, NoErrorCompletion completion);
}
