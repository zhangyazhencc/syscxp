package com.syscxp.sms;

public interface VerificationCode {

    void put(String uuid, String code);

    String get(String uuid);

    void remove(String uuid);

    void start();

    void stop();
}
