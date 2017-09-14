package org.zstack.sms;

public interface MailService {
    boolean ValidateMailCode(String mail, String code);
}
