package org.zstack.billing.identity;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

/**
 */
@GlobalConfigDefinition
public class BillingGlobalConfig {
    public static final String CATEGORY = "billing";

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig MAX_CONCURRENT_SESSION = new GlobalConfig(CATEGORY, "session.maxConcurrent");

    @GlobalConfigValidation(numberGreaterThan = 0,numberLessThan = 31536000)
    public static GlobalConfig SESSION_TIMEOUT = new GlobalConfig(CATEGORY, "session.timeout");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig SESSION_CLEANUP_INTERVAL = new GlobalConfig(CATEGORY, "session.cleanup.interval");

    @GlobalConfigValidation
    public static GlobalConfig SHOW_ALL_RESOURCE_TO_ADMIN = new GlobalConfig(CATEGORY, "admin.showAllResource");


}
