package com.syscxp.alarm;


import com.syscxp.core.GlobalPropertyDefinition;
import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigValidation;

@GlobalPropertyDefinition
public class AlarmGlobalConfig {

    public static final String CATEGORY = "alarm";

    @GlobalConfigValidation
    public static GlobalConfig EMAIL_TAG = new GlobalConfig(CATEGORY, "emailTag");

    @GlobalConfigValidation
    public static GlobalConfig PHONE_TAG = new GlobalConfig(CATEGORY, "phoneTag");
}
