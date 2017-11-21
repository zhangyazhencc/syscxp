package com.syscxp.vpn.vpn;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

import java.util.List;

@GlobalPropertyDefinition
public class VpnGlobalProperty {

    @GlobalProperty(name = "vpnMaxMotifies", defaultValue = "5")
    public static Integer VPN_MAX_MOTIFIES;


    @GlobalProperty(name="MonitorAgent.agentPackageName", defaultValue = "monitoragent-2.2.0.tar.gz")
    public static String AGENT_PACKAGE_NAME;
    @GlobalProperty(name="MonitorAgent.agentUrlRootPath", defaultValue = "")
    public static String AGENT_URL_ROOT_PATH;
    @GlobalProperty(name="MonitorAgent.agentUrlScheme", defaultValue = "http")
    public static String AGENT_URL_SCHEME;
    @GlobalProperty(name="MonitorAgent.port", defaultValue = "7079")
    public static int AGENT_PORT;
    @GlobalProperty(name="MonitorAgentServer.port", defaultValue = "10001")
    public static int AGENT_SERVER_PORT;
    @GlobalProperty(name="MonitorHost.iptables.rule.", defaultValue = "")
    public static List<String> IPTABLES_RULES;
    @GlobalProperty(name="MonitorAgent.syncOnHostPing", defaultValue = "true")
    public static Boolean AGENT_SYNC_ON_HOST_PING;

    @GlobalProperty(name = "vpn.vpnStatusCheckWorkerInterval", defaultValue = "60")
    public static int VPN_STATUS_CHECK_WORKER_INTERVAL;

}
