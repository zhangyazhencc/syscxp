package com.syscxp.tunnel.host;

import com.syscxp.header.core.NoErrorCompletion;
import com.syscxp.header.tunnel.host.MonitorHostInventory;

/**
 * Created by xing5 on 2016/8/6.
 */
public interface MonitorPingAgentNoFailureExtensionPoint {
    void monitorPingAgentNoFailure(MonitorHostInventory host, NoErrorCompletion completion);
}
