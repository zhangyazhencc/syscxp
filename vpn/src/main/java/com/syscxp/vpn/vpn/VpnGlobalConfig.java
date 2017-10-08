package com.syscxp.vpn.vpn;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class VpnGlobalConfig {
    public static final String CATEGORY = "vpn";

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig STATUS_CHECK_WORKER_INTERVAL = new GlobalConfig(CATEGORY, "vpn.statusCheckWorkerInterval");

}
