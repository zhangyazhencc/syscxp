package com.syscxp.sms;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class SmsGlobalProperty {

    @GlobalProperty(name = "smsYuntongxunServer", defaultValue = "app.cloopen.com")
    public static String SMS_YUNTONGXUN_SERVER;

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

    @GlobalProperty(name = "smsVerificationCodeAppid", defaultValue = "8a48b55149e0e7a20149ef2f176d0901")
    public static String SMS_VERIFICATION_CODE_APPID;

    @GlobalProperty(name = "smsVerificationCodeTemplateId", defaultValue = "8082")
    public static String SMS_VERIFICATION_CODE_TEMPLATEID;
}
