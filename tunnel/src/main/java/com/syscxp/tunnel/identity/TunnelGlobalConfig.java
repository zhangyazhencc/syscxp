package com.syscxp.tunnel.identity;

import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigDefinition;
import com.syscxp.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class TunnelGlobalConfig {
    public static final String CATEGORY = "tunnel";

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig PRODUCT_DELETE_DAYS = new GlobalConfig(CATEGORY, "productDelete.days");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig CHECK_TUNNEL_STATUS_INTERVAL = new GlobalConfig(CATEGORY, "tunnelStatus.interval");

    @GlobalConfigValidation
    public static GlobalConfig IS_CHECK_TUNNEL_STATUS = new GlobalConfig(CATEGORY, "isCheck.tunnelStatus");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig CLEAN_EXPIRED_PRODUCT_INTERVAL = new GlobalConfig(CATEGORY, "expiredProduct.cleanInterval");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig EXPIRED_PRODUCT_CLOSE_TIME = new GlobalConfig(CATEGORY, "expiredProduct.closeInterval");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig EXPIRED_PRODUCT_DELETE_TIME = new GlobalConfig(CATEGORY, "expiredProduct.deleteInterval");

    @GlobalConfigValidation
    public static GlobalConfig TRANSFER_RPC_IP = new GlobalConfig(CATEGORY, "transferRpcIp");
}
