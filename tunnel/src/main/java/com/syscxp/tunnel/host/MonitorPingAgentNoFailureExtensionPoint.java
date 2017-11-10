package com.syscxp.tunnel.host;

import com.syscxp.header.core.NoErrorCompletion;

/**
 * Created by xing5 on 2016/8/6.
 */
public interface MonitorPingAgentNoFailureExtensionPoint {
    void monitorPingAgentNoFailure(MonitorHostInventory host, NoErrorCompletion completion);
}
