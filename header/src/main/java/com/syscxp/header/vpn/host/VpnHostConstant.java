package com.syscxp.header.vpn.host;

import com.syscxp.utils.path.PathUtil;

public interface VpnHostConstant {

     String SERVICE_ID = "agent";

     String HOST_TYPE = "VPN";

     Integer HOST_START_PORT = 30000;
     String AGENT_CONNECT_PATH = "/host/connect";
     String AGENT_PING_PATH = "/host/ping";
     String AGENT_ECHO_PATH = "/host/echo";

     String ANSIBLE_PLAYBOOK_NAME = "vpn.py";
     String CREATE_CERT_PATH = PathUtil.getFilePathUndersyscxpHomeFolder("ansible/create_cert.py");
     String EASY_RSA_PATH = PathUtil.getFolderUndersyscxpHomeFolder("ansible/easy-rsa/keys");
     String ANSIBLE_MODULE_PATH = "ansible/vpn";

}
