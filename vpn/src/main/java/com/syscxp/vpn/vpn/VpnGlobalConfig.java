package com.syscxp.vpn.vpn;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class VpnGlobalConfig {
    public static final String CATEGORY = "vpn";

    @GlobalConfigValidation
    public static GlobalConfig TRANSFER_RPC_IP = new GlobalConfig(CATEGORY, "transferRpcIp");
    @GlobalConfigValidation
    public static GlobalConfig FALCON_API_IP = new GlobalConfig(CATEGORY, "falconApiIp");
}
