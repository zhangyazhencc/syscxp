package org.zstack.core.identity;

import org.zstack.core.GlobalProperty;
import org.zstack.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class IdentityGlobalProperty {

    @GlobalProperty(name = "accountServerUrl", defaultValue = "http://192.168.211.165:8080/api")
    public static String ACCOUNT_SERVER_URL;

}
