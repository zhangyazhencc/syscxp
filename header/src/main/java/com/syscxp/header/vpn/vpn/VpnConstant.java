package com.syscxp.header.vpn.vpn;

public interface VpnConstant {

    String SERVICE_ID = "vpn";
    String L3_SERVICE_ID = "l3vpn";
    String HOST_TYPE = "VPN";
    String URL_GENERATE_KEY = "asdjhsajfhasowjl234jhfv";
    String ACTION_CATEGORY_VPN = "vpn";
    String ACTION_CATEGORY_HOST = "host";
    String ACTION_SERVICE = "vpn";
    String KEYS_DIR = "keys";
    String CLIENT_CONF_PATH = "client.conf";
    String CLIENT_CRT_PATH = "client.crt";
    String CLIENT_KEY_PATH = "client.key";
    String CA_CRT_PATH = "ca.crt";
    String CA_KEY_PATH = "ca.key";
    String SERVER_CRT_PATH = "server.crt";
    String SERVER_KEY_PATH = "server.key";
    String DH1024_PEM_PATH = "dh1024.pem";

    String VPN_REPORT_PATH = "/report";
    String CONF_DOWNLOAD_PATH = "/conf/{uuid}";
    String CERT_DOWNLOAD_PATH = "/cert/{uuid}";

    String CREATE_CERT_PATH = "/vpn/create_cert";
    String VPN_CONF_PATH = "/vpn/conf_vpn";
    String RATE_LIMITING_PATH = "/vpn/rate_limiting";
    String VPN_PORT_PATH = "/vpn/vport";
    String VPN_SERVICE_PATH = "/vpn/vpn_service";
    String START_ALL_PATH = "/vpn/start_all";
    String DESTROY_VPN_PATH = "/vpn/destroy_vpn";
    String CLIENT_INFO_PATH = "/vpn/client_info";
    String LOGIN_INFO_PATH = "/vpn/login_info";
    String INIT_VPN_PATH = "/vpn/init_vpn";
    String PUSH_CERT_PATH = "/vpn/push_cert";
    String CHECK_VPN_STATUS_PATH = "/vpn/vpn_status";
    String L3_ROUTE_PATH = "/vpn/update_l3_route";
    String L3_IP_PATH = "/vpn/update_l3_network";

    String QUOTA_VPN_NUM = "vpn.num";
    String QUOTA_VPN_CERT_NUM = "vpnCert.num";
}
