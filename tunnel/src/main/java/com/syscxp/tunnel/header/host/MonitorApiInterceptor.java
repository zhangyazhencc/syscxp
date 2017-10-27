package com.syscxp.tunnel.header.host;

import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;


public class MonitorApiInterceptor implements ApiMessageInterceptor {

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateMonitorHostMsg) {
            validate((APICreateMonitorHostMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateMonitorHostMsg msg) {
        msg.setHostType(MonitorConstant.HOST_TYPE);
    }
}
