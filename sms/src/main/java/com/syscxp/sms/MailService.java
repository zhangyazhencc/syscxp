package com.syscxp.sms;

import java.util.List;

public interface MailService {
    boolean ValidateMailCode(String mail, String code);
    void sendAlarmMonitorMsg(List<String> mail, String subject, String comtent);

}
