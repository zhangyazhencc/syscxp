package com.syscxp.header.host;

public interface HostPingTaskExtensionPoint {
    void executeTaskAlongWithPingTask(HostInventory inv);

    HostType getHostType();
}
