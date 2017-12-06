package com.syscxp.vpn.host;

import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.vpn.host.VpnHostInventory;

public interface VpnHostPingAgentNoFailureExtensionPoint {
    void vpnPingAgentNoFailure(VpnHostInventory host, NoErrorCompletion completion);
}
