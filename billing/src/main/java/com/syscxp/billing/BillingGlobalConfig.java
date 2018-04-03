package com.syscxp.billing;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class BillingGlobalConfig {
    public static final String CATEGORY = "job";
    public static final String BILLJOBEXPRESSION = "billJob.cron";
    public static final String RENEWJOBEXPRESSION = "renewJob.cron";

    @GlobalConfigValidation
    public static GlobalConfig BILL_JOB = new GlobalConfig(CATEGORY, BILLJOBEXPRESSION);
    @GlobalConfigValidation
    public static GlobalConfig RENEW_JOB = new GlobalConfig(CATEGORY, RENEWJOBEXPRESSION);
}
