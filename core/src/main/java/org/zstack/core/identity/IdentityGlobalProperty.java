package org.zstack.core.identity;

import org.zstack.core.GlobalProperty;
import org.zstack.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class IdentityGlobalProperty {
    //@GlobalProperty(name = "accountServerUrl", defaultValue = "http://192.168.211.108:8080/api")
    @GlobalProperty(name = "accountServerUrl", defaultValue = "http://localhost:8081/api")
    public static String ACCOUNT_SERVER_URL;

}
