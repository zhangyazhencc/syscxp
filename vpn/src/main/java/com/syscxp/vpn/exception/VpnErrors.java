package com.syscxp.vpn.exception;

/**
 * @author wangjie
 */

public enum VpnErrors {
    //创建证书错误
    CREATE_CERT_ERRORS(1000),
    //调用billing失败
    CALL_BILLING_ERROR(1001),
    //VPN操作错误
    VPN_OPERATE_ERROR(1002),
    //初始化VPN错误
    INIT_VPN_ERROR(1003),
    //VPN配置错误
    VPN_CONF_ERROR(1004),
    //VPN重启错误
    VPN_RESTART_ERROR(1005),
    //VPN限速错误
    VPN_RATE_LIMIT_ERROR(1006),
    //证书上传错误
    PUSH_CERT_ERROR(1007),
    //销毁VPN错误
    VPN_DESTROY_ERROR(1008),
    //更新L3VPN路由错误
    L3_ROUTE_ERROR(1009);

    private String code;

    private VpnErrors(int id) {
        code = String.format("VPN.%s", id);
    }

    @Override
    public String toString() {
        return code;
    }
}
