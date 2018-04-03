package com.syscxp.vpn.l3vpn;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;

import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.vpn.agent.VpnMessage;

import com.syscxp.header.vpn.vpn.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.vpn.vpn.VpnBase;
import org.springframework.beans.factory.annotation.Autowired;

public class l3VpnManagerImpl extends AbstractService implements ApiMessageInterceptor {
    private static final CLogger LOGGER = Utils.getLogger(l3VpnManagerImpl.class);

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    @Override
    public void handleMessage(Message msg) {
        if (msg instanceof VpnMessage) {
            passThrough((VpnMessage) msg);
        } else if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
    }

    private void passThrough(VpnMessage msg) {
        VpnVO vo = dbf.findByUuid(msg.getVpnUuid(), VpnVO.class);
        if (vo == null) {
            String err = "Cannot find vpn: " + msg.getVpnUuid() + ", it may have been deleted";
            bus.replyErrorByMessageType(msg, err);
            return;
        }
        VpnBase base = new VpnBase(vo);
        base.handleMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateVpnMsg) {
            handle((APICreateVpnMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateVpnMsg msg) {
        final APICreateVpnEvent evt = new APICreateVpnEvent(msg.getId());
        doAddVpn(msg, new ReturnValueCompletion<VpnInventory>(msg) {
            @Override
            public void success(VpnInventory inventory) {
                evt.setInventory(inventory);
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
                bus.publish(evt);
            }
        });
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(VpnConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return null;
    }
}
