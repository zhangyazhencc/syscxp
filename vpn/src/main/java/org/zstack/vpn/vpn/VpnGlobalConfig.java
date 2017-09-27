package org.zstack.vpn.vpn;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class VpnGlobalConfig {
    public static final String CATEGORY = "vpn";

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig STATUS_CHECK_WORKER_INTERVAL = new GlobalConfig(CATEGORY, "vpn.statusCheckWorkerInterval");

}
