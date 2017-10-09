package com.syscxp.account.identity;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

/**
 */
@GlobalConfigDefinition
public class IdentityGlobalConfig {
    public static final String CATEGORY = "identity";

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig MAX_CONCURRENT_SESSION = new GlobalConfig(CATEGORY, "session.maxConcurrent");
    @GlobalConfigValidation(numberGreaterThan = 0, numberLessThan = 7200)
    public static GlobalConfig SESSION_TIMEOUT = new GlobalConfig(CATEGORY, "session.timeout");

    public static int SESSION_CLEANUP_INTERVAL = 3600;

//    @GlobalConfigValidation(notEmpty = false)
//    public static GlobalConfig ACCOUNT_API_CONTROL = new GlobalConfig(CATEGORY, "account.api.control");
}
