package com.syscxp.vpn.exception;

public enum VpnErrors {

    UNPAYABLE(1000),
    BILLING_ERROR(1001),
    VPN_OPERATE_ERROR(1002);

    private String code;

    private VpnErrors(int id) {
        code = String.format("VPN.%s", id);
    }

    @Override
    public String toString() {
        return code;
    }
}
