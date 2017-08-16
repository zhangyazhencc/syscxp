package org.zstack.sms;

import org.zstack.core.GlobalProperty;
import org.zstack.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class SmsGlobalProperty {

    @GlobalProperty(name = "smsYuntongxunServerUrl", defaultValue = "https://app.cloopen.com")
    public static String SMS_YUNTONGXUN_SERVER_URL;

    @GlobalProperty(name = "smsYuntongxunPort", defaultValue = "8883")
    public static String SMS_YUNTONGXUN_PORT;


    @GlobalProperty(name = "smsYuntongxunAccountSid", defaultValue = "aaf98f89499d24b50149c7033e70174a")
    public static String SMS_YUNTONGXUN_ACCOUNT_SID;

    @GlobalProperty(name = "smsYuntongxunAccountToken", defaultValue = "7645744968d7481b855a37bd248cc79a")
    public static String SMS_YUNTONGXUN_ACCOUNT_TOKEN;

    @GlobalProperty(name = "smsYuntongxunAppid", defaultValue = "aaf98f89499d24b50149cc2f108319f4")
    public static String SMS_YUNTONGXUN_APPID;

    @GlobalProperty(name = "smsTotalLimitPerDayPhone", defaultValue = "10")
    public static String TOTAL_LIMIT_PER_DAY_PHONE;

    @GlobalProperty(name = "smsTotalLimitPerDayIP", defaultValue = "50")
    public static String TOTAL_LIMIT_PER_DAY_IP;

    @GlobalProperty(name = "smsVerificationCodeAppid", defaultValue = "aaf98f89499d24b50149cc2f108319f4")
    public static String SMS_VERIFICATION_CODE_APPID;

    @GlobalProperty(name = "smsVerificationCodeTemplateId", defaultValue = "8082")
    public static String SMS_VERIFICATION_CODE_TEMPLATEID;
}
