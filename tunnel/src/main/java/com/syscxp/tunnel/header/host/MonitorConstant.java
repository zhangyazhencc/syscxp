package com.syscxp.tunnel.header.host;

public interface MonitorConstant {

    String SERVICE_ID = "agent";

    String HOST_TYPE = "MONITOR";

    String AGENT_CONNECT_PATH = "/host/connect";
    String AGENT_PING_PATH = "/host/ping";
    String AGENT_ECHO_PATH = "/host/echo";


    String ANSIBLE_PLAYBOOK_NAME = "monitor.py";
    String ANSIBLE_MODULE_PATH = "ansible/monitor";

    String AGENT_RECONNECT_ME = "/agent/reconnectme";
}
