package com.syscxp.test;

import com.syscxp.rest.RestServer;
import org.junit.Test;

import java.nio.file.Paths;

public class TestGenerateSDK {

    @Test
    public void test() {
        RestServer.generateJavaSdk("tunnel");
    }

}
