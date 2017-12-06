package com.syscxp.vpn.host;

import com.syscxp.header.core.Completion;
import com.syscxp.header.vpn.host.VpnHostInventory;

public interface VpnHostPingAgentExtensionPoint {
    void vpnPingAgent(VpnHostInventory host, Completion completion);
}
