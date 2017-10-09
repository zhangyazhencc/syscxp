package com.syscxp.core.notification;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

/**
 * Created by xing5 on 2017/5/2.
 */
@GlobalConfigDefinition
public class NotificationGlobalConfig {
    public static final String CATEGORY = "notification";

    //@GlobalConfigValidation(notNull = false)
    @GlobalConfigValidation
    public static GlobalConfig WEBHOOK_URL = new GlobalConfig(CATEGORY, "webhook.url");
}
