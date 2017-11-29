package com.syscxp.vpn.vpn;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

import java.util.List;

@GlobalPropertyDefinition
public class VpnGlobalProperty {

    @GlobalProperty(name = "vpnMaxMotifies", defaultValue = "5")
    public static Integer VPN_MAX_MOTIFIES;

    @GlobalProperty(name="VpnAgent.agentPackageName", defaultValue = "vpnagent-0.1.0.tar.gz")
    public static String AGENT_PACKAGE_NAME;
    @GlobalProperty(name="VpnAgent.agentUrlRootPath", defaultValue = "")
    public static String AGENT_URL_ROOT_PATH;
    @GlobalProperty(name="VpnAgent.agentUrlScheme", defaultValue = "http")
    public static String AGENT_URL_SCHEME;
    @GlobalProperty(name="VpnAgent.port", defaultValue = "7078")
    public static int AGENT_PORT;
    @GlobalProperty(name="VpnAgentServer.port", defaultValue = "10001")
    public static int AGENT_SERVER_PORT;
    @GlobalProperty(name="VpnHost.iptables.rule.", defaultValue = "")
    public static List<String> IPTABLES_RULES;
    @GlobalProperty(name="VpnAgent.syncOnHostPing", defaultValue = "true")
    public static Boolean AGENT_SYNC_ON_HOST_PING;

    @GlobalProperty(name = "vpn.vpnStatusCheckWorkerInterval", defaultValue = "60")
    public static int VPN_STATUS_CHECK_WORKER_INTERVAL;

}
