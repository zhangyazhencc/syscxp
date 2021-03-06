package com.syscxp.core.progress;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

/**
 * Created by xing5 on 2017/5/4.
 */
@GlobalConfigDefinition
public class ProgressGlobalConfig {
    public static final String CATEGORY = "progress";

    @GlobalConfigValidation
    public static GlobalConfig PROGRESS_ON = new GlobalConfig(CATEGORY, "progress.on");
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig PROGRESS_TTL = new GlobalConfig(CATEGORY, "progress.ttl");
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig CLEANUP_THREAD_INTERVAL = new GlobalConfig(CATEGORY, "progress.cleanupThreadInterval");
}
