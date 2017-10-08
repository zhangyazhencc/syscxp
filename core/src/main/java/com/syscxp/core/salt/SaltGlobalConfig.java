package com.syscxp.core.salt;

import com.syscxp.core.config.GlobalConfig;

/**
 */
public class SaltGlobalConfig {
    public static final String CATEGORY = "salt";

    public static GlobalConfig SETUP_MINION_IN_JOB = new GlobalConfig(CATEGORY, "minion.setupInJob");
    public static GlobalConfig SETUP_MINION_RETRY = new GlobalConfig(CATEGORY, "minion.applyState.retry");
    public static GlobalConfig SETUP_MINION_RETRY_INTERVAL = new GlobalConfig(CATEGORY, "minion.applyState.retry.interval");
}
