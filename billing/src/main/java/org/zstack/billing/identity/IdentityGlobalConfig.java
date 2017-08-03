package org.zstack.billing.identity;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

/**
 */
@GlobalConfigDefinition
public class IdentityGlobalConfig {
    public static final String CATEGORY = "identity";

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig MAX_CONCURRENT_SESSION = new GlobalConfig(CATEGORY, "session.maxConcurrent");
    @GlobalConfigValidation(numberGreaterThan = 0,numberLessThan = 31536000)
    public static GlobalConfig SESSION_TIMEOUT = new GlobalConfig(CATEGORY, "session.timeout");

    public static int SESSION_CLEANUP_INTERVAL = 3600;

}
