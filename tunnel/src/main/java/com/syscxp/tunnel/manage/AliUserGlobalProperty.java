package com.syscxp.tunnel.manage;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

@GlobalPropertyDefinition
public class AliUserGlobalProperty {

    @GlobalProperty(name = "vpnManagerUrl", defaultValue = "http://192.168.3.93:8000")
    public static String VPN_BASE_URL;
}
