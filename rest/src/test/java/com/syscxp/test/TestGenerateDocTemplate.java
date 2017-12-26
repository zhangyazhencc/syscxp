package com.syscxp.test;

import com.syscxp.rest.RestServer;
import com.syscxp.rest.sdk.DocumentGenerator;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Project: syscxp
 * Package: com.syscxp.test
 * Date: 2017/12/26 15:40
 * Author: wj
 */
class TestGenerateDocTemplate {

    @Test
    void test() {
        if (System.getProperty("recreate") != null) {
            RestServer.generateDocTemplate(Paths.get("../").toAbsolutePath().normalize().toString(), DocumentGenerator.DocMode.RECREATE_ALL);
        } else if (System.getProperty("repair") != null) {
            RestServer.generateDocTemplate(Paths.get("../").toAbsolutePath().normalize().toString(), DocumentGenerator.DocMode.REPAIR);
        } else {
            RestServer.generateDocTemplate(Paths.get("../").toAbsolutePath().normalize().toString(), DocumentGenerator.DocMode.CREATE_MISSING);
        }
    }
}
