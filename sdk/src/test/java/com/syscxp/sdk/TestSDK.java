package com.syscxp.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.testng.annotations.BeforeClass;

import java.util.concurrent.TimeUnit;

public class TestSDK {

    private String hostname = "192.168.211.99";
    private int port = 80;
    private String path = "tunnel";

    private ZSConfig zsConfig;

    private String SecretId = "accountqkx0aFFnstS37E0d";

    private String SecretKey = "MmX4b8ySs5wHrFPTKeFYfUOHB6CeF2";

    public String accountUuid = "873d6d56abf94079aebc0d131b930833";

    public Gson prettyGson;

    @BeforeClass
    public void setZsConfig() {
        zsConfig = new ZSConfig.Builder()
                .setHostname(hostname)
                .setPort(port)
                .setContextPath(path)
                .setDefaultPollingTimeout(10, TimeUnit.HOURS)
                .setSecret(SecretId, SecretKey)
                .build();
        ZSClient.configure(zsConfig);

        prettyGson = new GsonBuilder().setPrettyPrinting().create();
    }

}
