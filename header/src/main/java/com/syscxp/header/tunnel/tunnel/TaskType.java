package com.syscxp.header.tunnel.tunnel;

/**
 * Create by DCY on 2017/10/25
 */
public enum TaskType {
    Enabled,
    EnabledZK,

    Disabled,
    DisabledZK,

    ModifyBandwidth,

    ModifyPorts,
    ModifyPortsZK,

    Create,
    CreateZK,

    RollBackCreate,

    Delete,
    DeleteZK,

    Revert
}
