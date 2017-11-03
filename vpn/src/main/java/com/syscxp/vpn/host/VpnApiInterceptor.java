package com.syscxp.vpn.host;

import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.vpn.header.host.APICreateVpnHostMsg;


public class VpnApiInterceptor implements ApiMessageInterceptor {

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateVpnHostMsg) {
            validate((APICreateVpnHostMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateVpnHostMsg msg) {
        msg.setHostType(VpnHostConstant.HOST_TYPE);
    }
}
