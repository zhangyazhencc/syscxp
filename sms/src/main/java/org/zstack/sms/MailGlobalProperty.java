package org.zstack.sms;

import org.zstack.core.GlobalProperty;
import org.zstack.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class MailGlobalProperty {

    @GlobalProperty(name = "host", defaultValue = "smtp.exmail.qq.com")
    public static String HOST;

    @GlobalProperty(name = "from", defaultValue = "wangwg@syscloud.cn")
    public static String FROM;

    @GlobalProperty(name = "username", defaultValue = "wangwg@syscloud.cn")
    public static String USERNAME;

    @GlobalProperty(name = "password", defaultValue = "Aa123456789")
    public static String PASSWORD;

}
