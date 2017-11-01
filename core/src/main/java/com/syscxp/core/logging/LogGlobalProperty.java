package com.syscxp.core.logging;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class LogGlobalProperty {
    @GlobalProperty(name="Log.log4jBackendOn", defaultValue = "true")
    public static boolean LOG4j_BACKEND_ON;
    @GlobalProperty(name="Log.backend", defaultValue = "com.syscxp.core.logging.LogBackend")
    public static String LOGGING_BACKEND;

    @GlobalProperty(name = "Log.enabled", defaultValue = "true")
    public static Boolean ENABLED;
}
