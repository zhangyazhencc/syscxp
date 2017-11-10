package com.syscxp.core.host;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class HostGlobalProperty {

    @GlobalProperty(name = "Host.load.all", defaultValue = "true")
    public static Boolean SIMULTANEOUSLY_LOAD;
    @GlobalProperty(name = "Host.connection.autoReconnectOnError", defaultValue = "true")
    public static Boolean AUTO_RECONNECT_ON_ERROR;
    @GlobalProperty(name = "Host.load.parallelismDegree", defaultValue = "100")
    public static Integer HOST_LOAD_PARALLELISM_DEGREE;
    @GlobalProperty(name = "Host.ping.parallelismDegree", defaultValue = "100")
    public static Integer HOST_TRACK_PARALLELISM_DEGREE;
    @GlobalProperty(name = "Host.ping.interval", defaultValue = "60")
    public static Integer PING_HOST_INTERVAL;
    @GlobalProperty(name = "Host.maintenanceMode.ignoreError", defaultValue = "false")
    public static Boolean IGNORE_ERROR_ON_MAINTENANCE_MODE;
    @GlobalProperty(name = "Host.reconnectAllOnBoot", defaultValue = "true")  // 是否所有host都重连
    public static Boolean RECONNECT_ALL_ON_BOOT;


    @GlobalProperty(name = "Host.maxReconnectTimes", defaultValue = "5")
    public static int MAX_RECONNECT_TIMES;

}