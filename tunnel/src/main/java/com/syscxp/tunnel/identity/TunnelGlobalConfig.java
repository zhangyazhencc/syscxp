package com.syscxp.tunnel.identity;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class TunnelGlobalConfig {
    public static final String CATEGORY = "tunnel";

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig PRODUCT_DELETE_DAYS = new GlobalConfig(CATEGORY, "productDelete.days");
}
