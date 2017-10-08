package com.syscxp.core.ansible;

/**
 */
public interface AnsibleChecker {
    boolean needDeploy();

    void deleteDestFile();
}
