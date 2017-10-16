package com.syscxp.vpn.vpn;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

@GlobalPropertyDefinition
public class VpnGlobalProperty {

    @GlobalProperty(name = "vpnManagerUrl", defaultValue = "http://192.168.3.93:8000")
    public static String VPN_BASE_URL;

    @GlobalProperty(name = "billingServerUrl", defaultValue = "http://192.168.211.99:8082")
    public static String BILLING_SERVER_URL;

    @GlobalProperty(name = "vpnMaxMotifies", defaultValue = "5")
    public static Integer VPN_MAX_MOTIFIES;

}
