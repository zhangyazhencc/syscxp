package com.syscxp.rest;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

/**
 * Created by xing5 on 2016/12/9.
 */
@GlobalConfigDefinition
public class RestGlobalConfig {
    public static final String CATEGORY = "rest";

    @GlobalConfigValidation
    public static GlobalConfig COMPLETED_API_EXPIRED_PERIOD = new GlobalConfig(CATEGORY, "completedApi.expiredPeriod");
    @GlobalConfigValidation
    public static GlobalConfig SCAN_EXPIRED_API_INTERVAL = new GlobalConfig(CATEGORY, "expiredApi.scanInterval");
}
