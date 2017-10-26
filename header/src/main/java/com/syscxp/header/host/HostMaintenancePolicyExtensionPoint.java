package com.syscxp.header.host;

/**
 * Created by frank on 10/25/2015.
 */
public interface HostMaintenancePolicyExtensionPoint {
    public static enum HostMaintenancePolicy {
        MigrateVm,
        StopVm
    }

    HostMaintenancePolicy getHostMaintenancePolicy(HostInventory host);
}
