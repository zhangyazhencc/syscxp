package com.syscxp.header.tunnel.tunnel;

/**
 * Create by DCY on 2017/10/25
 */
public enum TaskType {
    Enabled,
    EnabledZK,

    Disabled,

    ModifyBandwidth,

    ModifyPorts,
    ModifyPortsZK,

    Create,

    Delete,
    DeleteZK,

    Revert
}
