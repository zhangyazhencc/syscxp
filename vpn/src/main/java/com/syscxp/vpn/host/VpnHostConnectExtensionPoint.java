package com.syscxp.vpn.host;

import com.syscxp.header.core.workflow.Flow;

/**
 * @author wangjie
 */
public interface VpnHostConnectExtensionPoint {
    /**
     * @param context Vpn连接上下文
     * @return Flow
     */
    Flow createHostConnectingFlow(VpnHostConnectedContext context);
}
