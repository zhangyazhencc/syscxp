package com.syscxp.sms;

/**
 * Created by zxhread on 17/8/16.
 */
public interface SmsService {

    boolean validateVerificationCode(String phone, String code);
}
