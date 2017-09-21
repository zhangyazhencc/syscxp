package org.zstack.vpn.manage;

import org.zstack.core.GlobalProperty;
import org.zstack.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class VpnGlobalProperty {

    @GlobalProperty(name = "vpnManagerUrl", defaultValue = "http://192.168.211.108:8080")
    public static String VPN_CONTROLLER_URL;

    @GlobalProperty(name = "billingServerUrl", defaultValue = "http://192.168.211.99:8082")
    public static String BILLING_SERVER_URL;
}

