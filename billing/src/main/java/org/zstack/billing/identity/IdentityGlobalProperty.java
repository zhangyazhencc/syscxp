package org.zstack.billing.identity;

import org.zstack.core.GlobalProperty;
import org.zstack.core.GlobalPropertyDefinition;
import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

/**
 */
@GlobalPropertyDefinition
public class IdentityGlobalProperty {

    @GlobalProperty(name = "accountServerUrl", defaultValue = "http:// 192.168.211.165:8080/api")
    public static String ACCOUNT_SERVER_URL;

    public static int SESSION_CLEANUP_INTERVAL = 3600;

}

