package org.zstack.sms;

import org.zstack.core.GlobalProperty;
import org.zstack.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class MailGlobalProperty {

    @GlobalProperty(name = "host", defaultValue = "smtp.163.com")
    public static String HOST;

    @GlobalProperty(name = "from", defaultValue = "wangwg@syscloud.cn")
    public static String FROM;

    @GlobalProperty(name = "username", defaultValue = "wangwg@syscloud.cn")
    public static String USERNAME;

    @GlobalProperty(name = "password", defaultValue = "wang88v5")
    public static String PASSWORD;

}
