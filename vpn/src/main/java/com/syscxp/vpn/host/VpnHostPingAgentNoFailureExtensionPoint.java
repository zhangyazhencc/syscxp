package com.syscxp.vpn.host;

import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.vpn.host.VpnHostInventory;

/**
 * @author wangjie
 */
public interface VpnHostPingAgentNoFailureExtensionPoint {
    /**
     * @param host Host清单
     * @param completion noError回调
     */
    void vpnPingAgentNoFailure(VpnHostInventory host, NoErrorCompletion completion);
}
