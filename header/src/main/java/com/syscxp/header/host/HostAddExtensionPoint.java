package com.syscxp.header.host;

import com.syscxp.header.core.Completion;

/**
 */
public interface HostAddExtensionPoint {
    void beforeAddHost(HostInventory host, Completion completion);

    void afterAddHost(HostInventory host, Completion completion);
}
