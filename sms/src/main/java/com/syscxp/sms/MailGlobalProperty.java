package com.syscxp.sms;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class MailGlobalProperty {

    @GlobalProperty(name = "mailHost", defaultValue = "smtp.exmail.qq.com")
    public static String MAIL_HOST;

    @GlobalProperty(name = "mailFrom", defaultValue = "notice@syscloud.cn")
    public static String MAIL_FROM;

    @GlobalProperty(name = "mailUsername", defaultValue = "notice@syscloud.cn")
    public static String MAIL_USERNAME;

    @GlobalProperty(name = "mailPassword", defaultValue = "Aa123456789")
    public static String MAIL_PASSWORD;

}
