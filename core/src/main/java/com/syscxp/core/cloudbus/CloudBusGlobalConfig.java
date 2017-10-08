package com.syscxp.core.cloudbus;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

/**
 */
@GlobalConfigDefinition
public class CloudBusGlobalConfig {
    public static final String CATEGORY = "cloudBus";

    @GlobalConfigValidation
    public static GlobalConfig STATISTICS_ON = new GlobalConfig(CATEGORY, "statistics.on");
}
