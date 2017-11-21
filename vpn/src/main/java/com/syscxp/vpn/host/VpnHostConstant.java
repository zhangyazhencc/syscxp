package com.syscxp.vpn.host;

public interface VpnHostConstant {

     String SERVICE_ID = "zone";

     String HOST_TYPE = "VPN";

     Integer HOST_START_PORT = 30000;
     String AGENT_CONNECT_PATH = "/host/connect";
     String AGENT_PING_PATH = "/host/ping";
     String AGENT_ECHO_PATH = "/host/echo";

     String ANSIBLE_PLAYBOOK_NAME = "vpn.py";
     String ANSIBLE_MODULE_PATH = "ansible/vpn";
     String AGENT_RECONNECT_ME = "/agent/reconnectme";

     String ACTION_CATEGORY_VPN = "vpn-host";
     String CHECK_HOST_STATUS_PATH = "/agent-status/";
     String ADD_HOST_PATH = "/add-host/";
     String Delete_HOST_PATH = "/del-host/";
     String RECONNECT_HOST_PATH = "/reconnect/";

}
