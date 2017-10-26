package com.syscxp.core.host;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

/**
 */
@GlobalConfigDefinition
public class HostGlobalConfig {
    public static final String CATEGORY = "host";

    @GlobalConfigValidation
    public static GlobalConfig SIMULTANEOUSLY_LOAD = new GlobalConfig(CATEGORY, "load.all");
    @GlobalConfigValidation
    public static GlobalConfig AUTO_RECONNECT_ON_ERROR = new GlobalConfig(CATEGORY, "connection.autoReconnectOnError");
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig HOST_LOAD_PARALLELISM_DEGREE = new GlobalConfig(CATEGORY, "load.parallelismDegree");
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig HOST_TRACK_PARALLELISM_DEGREE = new GlobalConfig(CATEGORY, "ping.parallelismDegree");
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig PING_HOST_INTERVAL = new GlobalConfig(CATEGORY, "ping.interval");
    @GlobalConfigValidation
    public static GlobalConfig IGNORE_ERROR_ON_MAINTENANCE_MODE = new GlobalConfig(CATEGORY, "maintenanceMode.ignoreError");
    @GlobalConfigValidation  // 是否所有host都重连
    public static GlobalConfig RECONNECT_ALL_ON_BOOT = new GlobalConfig(CATEGORY, "reconnectAllOnBoot");
}
