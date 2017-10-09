package com.syscxp.core.identity;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class IdentityGlobalProperty {
    //@GlobalProperty(name = "accountServerUrl", defaultValue = "http://192.168.211.108:8080/api")
    @GlobalProperty(name = "accountServerUrl", defaultValue = "http://localhost:8081/api")
    public static String ACCOUNT_SERVER_URL;

}
