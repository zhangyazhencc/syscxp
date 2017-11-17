package com.syscxp.header.tunnel;

import com.syscxp.header.configuration.PythonClass;

/**
 * Created by DCY on 8/7/17.
 */

@PythonClass
public interface TunnelConstant {

    String SERVICE_ID = "tunnel";
    String ACTION_CATEGORY = "tunnel";
    String TUNNEL_ROOT_PATH = "tunnel";
//    String NOTIFYURL = "http://192.168.211.97:8888/tunnel/asyncrest/sendcommand";


    String QUOTA_INTERFACE_NUM = "interface.num";
    String QUOTA_INTERFACE_BANDWIDTH = "interface.bandwidth";

    String QUOTA_TUNNEL_NUM = "tunnel.num";
    String QUOTA_TUNNEL_BANDWIDTH = "tunnel.bandwidth";
}
