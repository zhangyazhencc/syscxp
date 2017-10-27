package com.syscxp.header.host;

public interface HostConnectionReestablishExtensionPoint {
    void connectionReestablished(HostInventory inv) throws HostException;

    HostType getHostTypeForReestablishExtensionPoint();
}
