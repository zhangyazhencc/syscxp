package com.syscxp.sdk;

import java.util.concurrent.TimeUnit;

/**
 * Project: syscxp
 * Package: com.syscxp.sdk
 * Date: 2017/12/26 14:06
 * Author: wj
 */
public class ZSConfig {
    String scheme = "http";
    String hostname = "api.syscxp.com";
    int port = 80;
    long defaultPollingTimeout = TimeUnit.HOURS.toMillis(3);
    long defaultPollingInterval = TimeUnit.SECONDS.toMillis(1);
    String webHook;
    Long readTimeout;
    Long writeTimeout;
    String contextPath;
    String SecretKey;
    String SecretId;
    String SignatureMethod = "HmacMD5";

    public String getScheme() {
        return scheme;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public long getDefaultPollingTimeout() {
        return defaultPollingTimeout;
    }

    public long getDefaultPollingInterval() {
        return defaultPollingInterval;
    }

    public static class Builder {
        ZSConfig config = new ZSConfig();

        public Builder setScheme(String scheme) {
            config.scheme = scheme;
            return this;
        }

        public Builder setHostname(String hostname) {
            config.hostname = hostname;
            return this;
        }

        public Builder setWebHook(String webHook) {
            config.webHook = webHook;
            return this;
        }

        public Builder setPort(int port) {
            config.port = port;
            return this;
        }

        public Builder setSecret(String SecretId, String SecretKey ){
            config.SecretId = SecretId;
            config.SecretKey = SecretKey;
            return this;
        }

        public Builder setSignatureMethod(String SignatureMethod) {
            config.SignatureMethod = SignatureMethod;
            return this;
        }

        public Builder setDefaultPollingTimeout(long value, TimeUnit unit) {
            config.defaultPollingTimeout = unit.toMillis(value);
            return this;
        }

        public Builder setDefaultPollingInterval(long value, TimeUnit unit) {
            config.defaultPollingInterval = unit.toMillis(value);
            return this;
        }

        public Builder setReadTimeout(long value, TimeUnit unit) {
            config.readTimeout = unit.toMillis(value);
            return this;
        }

        public Builder setWriteTimeout(long value, TimeUnit unit) {
            config.writeTimeout = unit.toMillis(value);
            return this;
        }

        public Builder setContextPath(String name) {
            config.contextPath = name;
            return this;
        }


        public ZSConfig build() {
            return config;
        }
    }
}
