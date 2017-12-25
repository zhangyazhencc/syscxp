package com.syscxp.vpn.host;

import com.syscxp.core.db.Q;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.host.APIDeleteHostMsg;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.vpn.host.*;
import com.syscxp.header.vpn.vpn.VpnVO;
import com.syscxp.header.vpn.vpn.VpnVO_;

import static com.syscxp.core.Platform.argerr;

/**
 * @author wangjie
 */
public class VpnHostApiInterceptor implements ApiMessageInterceptor {

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateVpnHostMsg) {
            validate((APICreateVpnHostMsg) msg);
        } else if (msg instanceof APIUpdateVpnHostMsg) {
            validate((APIUpdateVpnHostMsg) msg);
        } else if (msg instanceof APIDeleteHostMsg) {
            validate((APIDeleteHostMsg) msg);
        }
        return msg;
    }

    private void validate(APIDeleteHostMsg msg) {
        Q q = Q.New(VpnVO.class)
                .eq(VpnVO_.uuid, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr(
                    "The VpnHost[uuid:%s] has vpn server.", msg.getUuid()
            ));
        }

    }


    private void validate(APICreateVpnHostMsg msg) {
        Q q = Q.New(VpnHostVO.class)
                .eq(VpnHostVO_.name, msg.getName());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr(
                    "The VpnHost[name:%s] is already exist.", msg.getName()
            ));
        }
        msg.setHostType(VpnHostConstant.HOST_TYPE);
    }

    private void validate(APIUpdateVpnHostMsg msg) {
        Q q = Q.New(VpnHostVO.class)
                .eq(VpnHostVO_.name, msg.getName());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr(
                    "The VpnHost[name:%s] is already exist.", msg.getName()
            ));
        }
    }

}
