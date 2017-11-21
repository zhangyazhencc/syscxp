package com.syscxp.vpn.host;

import com.syscxp.header.core.Completion;
import com.syscxp.header.vpn.host.VpnHostInventory;

public interface VpnPingAgentExtensionPoint {
    void vpnPingAgent(VpnHostInventory host, Completion completion);
}
