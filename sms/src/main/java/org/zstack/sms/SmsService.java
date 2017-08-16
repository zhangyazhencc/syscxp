package org.zstack.sms;

/**
 * Created by zxhread on 17/8/16.
 */
public interface SmsService {

    boolean ValidateVerificationCode(String phone, String code);
}
