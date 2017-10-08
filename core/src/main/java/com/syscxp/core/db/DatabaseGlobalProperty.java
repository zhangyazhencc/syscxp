package com.syscxp.core.db;

import com.syscxp.core.GlobalPropertyDefinition;
import com.syscxp.core.GlobalProperty;

/**
 */
@GlobalPropertyDefinition
public class DatabaseGlobalProperty {
    @GlobalProperty(name="DatabaseFacade.deadlockRetryTimes", defaultValue = "10")
    public static int retryTimes;
    @GlobalProperty(name="DB.url")
    public static String DbUrl;
    @GlobalProperty(name="DB.restUrl")
    public static String DbRestUrl;
    @GlobalProperty(name="DB.user")
    public static String DbUser;
    @GlobalProperty(name="DB.password")
    public static String DbPassword;
    @GlobalProperty(name="DB.idleConnectionTestPeriod")
    public static String DbIdleConnectionTestPeriod;
    @GlobalProperty(name="DB.maxIdleTime")
    public static String DbMaxIdleTime;
}
