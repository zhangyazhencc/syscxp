package com.syscxp.sms;

public interface MailService {
    boolean ValidateMailCode(String mail, String code);
    boolean mailSend(String mail, String subject, String comtent);
}
