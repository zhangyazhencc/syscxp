package com.syscxp.vpn.host;

public interface VpnHostConstant {

    public static final String SERVICE_ID = "vpn-host";

    public static final String HOST_TYPE = "VPN";

    public static final Integer HOST_START_PORT = 30000;
    public static final String AGENT_CONNECT_PATH = "/host/connect";
    public static final String AGENT_PING_PATH = "/host/ping";
    public static final String AGENT_ECHO_PATH = "/host/echo";

    public static final String ANSIBLE_PLAYBOOK_NAME = "monitor.py";
    public static final String ANSIBLE_MODULE_PATH = "ansible/monitor";
    public static final String AGENT_RECONNECT_ME = "/agent/reconnectme";

    public static final String ACTION_CATEGORY_VPN = "vpn-host";

    public static final String CHECK_HOST_STATUS_PATH = "/agent-status/";

    public static final String ADD_HOST_PATH = "/add-host/";

    public static final String Delete_HOST_PATH = "/del-host/";

    public static final String RECONNECT_HOST_PATH = "/reconnect/";

}
