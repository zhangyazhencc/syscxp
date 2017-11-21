package com.syscxp.header.tunnel;

import com.syscxp.header.configuration.PythonClass;

/**
 * Created by DCY on 8/7/17.
 */

@PythonClass
public interface TunnelConstant {

    String SERVICE_ID = "tunnel";
    String ACTION_SERVICE = "tunnel";
    String ACTION_CATEGORY = "tunnel";

    String QUOTA_INTERFACE_NUM = "interface.num";
    String QUOTA_TUNNEL_NUM = "tunnel.num";
}
