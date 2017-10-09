package com.syscxp.core.config;

public interface GlobalConfigUpdateExtensionPoint {
    void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig);
}
