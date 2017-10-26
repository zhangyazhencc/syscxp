package com.syscxp.tunnel.header.host;

public interface MonitorConstant {

    String SERVICE_ID = "agent";

    String HOST_TYPE = "MONITOR";

    String AGENT_CONNECT_PATH = "/host/connect";
    String AGENT_PING_PATH = "/host/ping";
    String AGENT_ECHO_PATH = "/host/echo";


    String ANSIBLE_PLAYBOOK_NAME = "monitor.py";
    String ANSIBLE_MODULE_PATH = "ansible/monitor";

    String AGENT_REPORT_STATE = "/agent/reportstate";
    String AGENT_RECONNECT_ME = "/agent/reconnectme";
    String AGENT_REPORT_PS_STATUS = "/agent/reportstoragestatus";
    String AGENT_ANSIBLE_LOG_PATH_FROMAT = "/agent/ansiblelog/{uuid}";

}
