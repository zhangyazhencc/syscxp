package com.syscxp.vpn.vpn;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

/**
 * @author wangjie
 */
@GlobalConfigDefinition
public class VpnGlobalConfig {
    public static final String CATEGORY = "vpn";

    @GlobalConfigValidation
    public static GlobalConfig TRANSFER_RPC_IP = new GlobalConfig(CATEGORY, "transferRpcIp");
    @GlobalConfigValidation
    public static GlobalConfig FALCON_API_IP = new GlobalConfig(CATEGORY, "falconApiIp");
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig CLEAN_EXPIRED_VPN_INTERVAL = new GlobalConfig(CATEGORY, "expiredVpn.cleanInterval");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig EXPIRED_VPN_DELETE_TIME = new GlobalConfig(CATEGORY, "expiredVpn.deleteInterval");
}
