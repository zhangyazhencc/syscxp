package com.syscxp.core.host;

import com.syscxp.header.core.workflow.Flow;
import com.syscxp.header.host.HostInventory;

/**
 * Created by miao on 16-7-20.
 */
public interface PostHostConnectExtensionPoint {
    Flow createPostHostConnectFlow(HostInventory host);
}
