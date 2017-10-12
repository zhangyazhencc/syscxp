package com.syscxp.vpn.exception;

public enum VpnErrors {

    UNPAYABLE(1001),
    BILLING_ERROR(1002),
    VPN_CONTROLLER_ERROR(1003);

    private String code;

    private VpnErrors(int id) {
        code = String.format("VPN.%s", id);
    }

    @Override
    public String toString() {
        return code;
    }
}
