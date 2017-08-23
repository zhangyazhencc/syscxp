package org.zstack.tunnel.identity;

import org.zstack.core.GlobalProperty;
import org.zstack.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class IdentityGlobalProperty {

    @GlobalProperty(name = "accountServerUrl", defaultValue = "http://192.168.211.165:8080/api")
    public static String ACCOUNT_SERVER_URL;

    public static int SESSION_CLEANUP_INTERVAL = 3600;

}
