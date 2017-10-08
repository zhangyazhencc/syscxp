package com.syscxp.core.encrypt;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

/**
 * Created by mingjian.deng on 16/12/26.
 */
@GlobalConfigDefinition
public class EncryptGlobalConfig {
    public static final String CATEGORY = "encrypt";

    public static final String SERVICE_ID = "encrypt";

    @GlobalConfigValidation
    public static GlobalConfig ENCRYPT_ALGORITHM = new GlobalConfig(CATEGORY, "encrypt.algorithm");
}
