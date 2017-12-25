package com.syscxp.vpn.host;

import com.syscxp.header.core.Completion;
import com.syscxp.header.vpn.host.VpnHostInventory;

/**
 * @author wangjie
 */
public interface VpnHostPingAgentExtensionPoint {
    /**
     * @param host Host清单
     * @param completion 回调
     */
    void vpnPingAgent(VpnHostInventory host, Completion completion);
}
