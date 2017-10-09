package com.syscxp.core.salt;

/**
 */
public interface SaltFacade {
    void deployModule(String modulePath);

    SaltRunner createSaltRunner();

    boolean isModuleChanged(String moduleName);
}
