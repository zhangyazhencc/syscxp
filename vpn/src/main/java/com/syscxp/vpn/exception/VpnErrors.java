package com.syscxp.vpn.exception;

public enum VpnErrors {

    INSUFFICIENT_BALANCE(2000);

    private String code;

    private VpnErrors(int id) {
        code = String.format("VPN.%s", id);
    }

    @Override
    public String toString() {
        return code;
    }
}
