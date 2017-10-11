package com.syscxp.vpn.vpn;

public interface VpnConstant {

    public static final String SERVICE_ID = "vpn";



    public static final String ACTION_CATEGORY_VPN = "vpn";

    public static final String VPN_ROOT_PATH = "vpn";

    public static final String INIT_VPN_PATH = "/init-vpn/";

    public static final String DELETE_VPN_PATH = "/destroy-vpn/";

    public static final String STOP_VPN_PATH = "/close-vpn/";

    public static final String START_VPN_PATH = "/start-vpn/";

    public static final String UPDATE_VPN_CIDR_PATH = "/update-cidr/";

    public static final String UPDATE_VPN_BANDWIDTH_PATH = "/update-bandwith/";

    public static final String ADD_VPN_INTERFACE_PATH = "/add-ddn-if/";

    public static final String DELETE_VPN_INTERFACE_PATH = "/del-ddn-if/";

    public static final String ADD_VPN_ROUTE_PATH = "/add-route/";

    public static final String DELETE_VPN_ROUTE_PATH = "/del-route/";

    public static final String CHECK_VPN_STATUS_PATH = "/vpn-status/";

    public static final String RECONNECT_VPN_PATH = "/reconnect-vpn/";

    public static final String CHECK_CREATE_STATE_PATH = "/result/";

    public static final String DOWNLOAD_CERTIFICATE_PATH = "/client-conf/";

    public static final String RESET_CERTIFICATE_PATH = "/reset-cert/";
}
