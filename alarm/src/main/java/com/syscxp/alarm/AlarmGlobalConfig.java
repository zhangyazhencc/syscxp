package com.syscxp.alarm;


import com.syscxp.core.GlobalPropertyDefinition;
import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class AlarmGlobalConfig {

    public static final String CATEGORY = "alarm";

    @GlobalConfigValidation
    public static GlobalConfig ALARM_SEND_MAIL = new GlobalConfig(CATEGORY, "alarm.sendMail");

    @GlobalConfigValidation
    public static GlobalConfig ALARM_SEND_SMS = new GlobalConfig(CATEGORY, "alarm.sendSms");
}
