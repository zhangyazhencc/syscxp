package com.syscxp.test;

import com.syscxp.rest.RestServer;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Project: syscxp
 * Package: com.syscxp.test
 * Date: 2017/12/26 15:39
 * Author: wj
 */
class TestGenerateMarkDownDoc {

    @Test
    public void test() {
        RestServer.generateMarkdownDoc(Paths.get("../").toAbsolutePath().normalize().toString());
    }
}
