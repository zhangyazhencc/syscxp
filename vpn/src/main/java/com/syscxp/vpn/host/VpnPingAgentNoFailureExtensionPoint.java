package com.syscxp.vpn.host;

import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.vpn.header.host.VpnHostInventory;

/**
 * Created by xing5 on 2016/8/6.
 */
public interface VpnPingAgentNoFailureExtensionPoint {
    void vpnPingAgentNoFailure(VpnHostInventory host, NoErrorCompletion completion);
}
