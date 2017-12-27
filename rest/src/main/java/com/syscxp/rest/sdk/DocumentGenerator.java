package com.syscxp.rest.sdk;

/**
 * Project: syscxp
 * Package: com.syscxp.rest.sdk
 * Date: 2017/12/26 14:54
 * Author: wj
 */
public interface DocumentGenerator {
    enum DocMode {
        RECREATE_ALL,
        CREATE_MISSING,
        REPAIR,
    }

    void generateDocTemplates(String scanPath, DocMode mode);

    void generateMarkDown(String scanPath, String resultDir);
}