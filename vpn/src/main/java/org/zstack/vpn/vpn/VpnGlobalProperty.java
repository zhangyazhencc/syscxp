package org.zstack.vpn.vpn;

import org.zstack.core.GlobalProperty;
import org.zstack.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class VpnGlobalProperty {

    @GlobalProperty(name = "vpnManagerUrl", defaultValue = "http://192.168.211.200:8080")
    public static String VPN_BASE_URL;

    @GlobalProperty(name = "billingServerUrl", defaultValue = "http://192.168.211.99:8082/billing")
    public static String BILLING_SERVER_URL;
}

