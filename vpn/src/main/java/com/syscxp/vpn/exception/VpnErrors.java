package com.syscxp.vpn.exception;

public enum VpnErrors {

    CREATE_CERT_ERRORS(1000),
    CALL_BILLING_ERROR(1001),
    VPN_OPERATE_ERROR(1002),
    INIT_VPN_ERROR(1003),
    VPN_CONF_ERROR(1004),
    VPN_RESTART_ERROR(1005),
    VPN_RATE_LIMIT_ERROR(1006),
    PUSH_CERT_ERROR(1007),
    VPN_DESTROY_ERROR(1008);


    private String code;

    private VpnErrors(int id) {
        code = String.format("VPN.%s", id);
    }

    @Override
    public String toString() {
        return code;
    }
}
