package com.syscxp.header.tunnel;

import com.syscxp.header.configuration.PythonClass;

/**
 * SLA
 *
 * @author sunxuelong
 * @date 2018/05/09
 */
public interface SlaConstant {

    String SERVICE_ID = "sla";
    String ACTION_CATEGORY = "sla";

    long INTERVAL = 300;
    String TUNNEL_METRIC = "tunnel.packets.lost.NEW.5m";
}
