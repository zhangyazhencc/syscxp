package com.syscxp.sms;

public interface MailService {
    boolean ValidateMailCode(String mail, String code);
}
