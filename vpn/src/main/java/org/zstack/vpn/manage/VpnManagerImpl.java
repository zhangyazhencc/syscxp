package org.zstack.vpn.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.vpn.header.APICreateVpnHostMsg;
import org.zstack.vpn.header.APIDeleteVpnHostMsg;
import org.zstack.vpn.header.APIQueryVpnHostMsg;
import org.zstack.vpn.header.APIUpdateVpnHostMsg;

public class VpnManagerImpl extends AbstractService implements VpnManager, ApiMessageInterceptor {

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateVpnHostMsg) {
            handle((APICreateVpnHostMsg) msg);
        } else if (msg instanceof APIUpdateVpnHostMsg) {
            handle((APIUpdateVpnHostMsg) msg);
        } else if (msg instanceof APIDeleteVpnHostMsg) {
            handle((APIDeleteVpnHostMsg) msg);
        } else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIDeleteVpnHostMsg msg) {

    }

    private void handle(APIUpdateVpnHostMsg msg) {

    }

    private void handle(APICreateVpnHostMsg msg) {

    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }


    public String getId() {
        return bus.makeLocalServiceId(VpnConstant.SERVICE_ID);
    }

    public boolean start() {
        return true;
    }

    public boolean stop() {
        return true;
    }

    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateVpnHostMsg) {
            validate((APICreateVpnHostMsg) msg);
        } else if (msg instanceof APIUpdateVpnHostMsg) {
            validate((APIUpdateVpnHostMsg) msg);
        } else if (msg instanceof APIDeleteVpnHostMsg) {
            validate((APIDeleteVpnHostMsg) msg);
        } else if (msg instanceof APIQueryVpnHostMsg) {
            validate((APIQueryVpnHostMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateVpnHostMsg msg) {
    }
    private void validate(APIUpdateVpnHostMsg msg) {
    }
    private void validate(APIDeleteVpnHostMsg msg) {
    }
    private void validate(APIQueryVpnHostMsg msg) {
    }
}
