package com.syscxp.core.host;

import com.syscxp.header.tunnel.host.MonitorHostVO;
import com.syscxp.header.tunnel.host.MonitorHostVO_;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.SimpleQuery.Op;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.apimediator.StopRoutingException;
import com.syscxp.header.host.*;
import com.syscxp.header.message.APIMessage;
import com.syscxp.utils.network.NetworkUtils;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.core.Platform.operr;

public class HostApiInterceptor implements ApiMessageInterceptor {
    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private DatabaseFacade dbf;

    private void setServiceId(APIMessage msg) {
        if (msg instanceof HostMessage) {
            HostMessage hmsg = (HostMessage)msg;
            bus.makeLocalServiceId(msg, HostConstant.SERVICE_ID);
        }
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        setServiceId(msg);

        if (msg instanceof APIAddHostMsg) {
            validate((APIAddHostMsg) msg);
        } else if (msg instanceof APIUpdateHostMsg) {
            validate((APIUpdateHostMsg) msg);
        } else if (msg instanceof APIDeleteHostMsg) {
            validate((APIDeleteHostMsg) msg);
        } else if (msg instanceof APIChangeHostStateMsg){
            validate((APIChangeHostStateMsg) msg);
        }

        return msg;
    }

    private void validate(APIDeleteHostMsg msg) {
        if (!dbf.isExist(msg.getUuid(), HostVO.class)) {
            APIDeleteHostEvent evt = new APIDeleteHostEvent(msg.getId());
            bus.publish(evt);
            throw new StopRoutingException();
        }
    }

    private void validate(APIUpdateHostMsg msg) {
        HostStatus hostStatus = Q.New(HostVO.class)
                .select(HostVO_.status)
                .eq(HostVO_.uuid,msg.getHostUuid())
                .findValue();
        if (hostStatus == HostStatus.Connecting){
            throw new ApiMessageInterceptionException(
                    operr("can not update host[uuid:%s]which is connecting or creating, please wait.", msg.getHostUuid()));
        }
    }

    private void validate(APIAddHostMsg msg) {
        if (!NetworkUtils.isIpv4Address(msg.getHostIp()) && !NetworkUtils.isHostname(msg.getHostIp())) {
            throw new ApiMessageInterceptionException(argerr("managementIp[%s] is neither an IPv4 address nor a valid hostname", msg.getHostIp()));
        }
        Q q = Q.New(HostVO.class).eq(HostVO_.hostIp, msg.getHostIp());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("there has been a host having management ip[%s]", msg.getHostIp()));
        }
        q = Q.New(HostVO.class).eq(HostVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The Host[name:%s] is already exist.", msg.getName()
            ));

        //判断code是否已经存在
        q = Q.New(HostVO.class).eq(HostVO_.code, msg.getCode());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr("host's code %s is already exist ", msg.getCode()));
    }

    private void validate(APIChangeHostStateMsg msg){
        HostStatus hostStatus = Q.New(HostVO.class)
                .select(HostVO_.status)
                .eq(HostVO_.uuid,msg.getHostUuid())
                .findValue();
        if (hostStatus == HostStatus.Connecting && msg.getStateEvent().equals(HostStateEvent.maintain.toString())){
            throw new ApiMessageInterceptionException(operr("can not maintain host[uuid:%s]which is connecting", msg.getHostUuid()));
        }
    }
}
