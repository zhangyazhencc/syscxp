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
import org.zstack.vpn.header.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;

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
        } else if (msg instanceof APICreateVpnGatewayMsg) {
            handle((APICreateVpnGatewayMsg) msg);
        } else if (msg instanceof APIUpdateVpnGatewayMsg) {
            handle((APIUpdateVpnGatewayMsg) msg);
        } else if (msg instanceof APIUpdateVpnBindwidthMsg) {
            handle((APIUpdateVpnBindwidthMsg) msg);
        } else if (msg instanceof APIDeleteVpnGatewayMsg) {
            handle((APIDeleteVpnGatewayMsg) msg);
        } else if (msg instanceof APICreateTunnelIfaceMsg) {
            handle((APICreateTunnelIfaceMsg) msg);
        } else if (msg instanceof APIUpdateTunnelIfaceMsg) {
            handle((APIUpdateTunnelIfaceMsg) msg);
        } else if (msg instanceof APIDeleteTunnelIfaceMsg) {
            handle((APIDeleteTunnelIfaceMsg) msg);
        } else if (msg instanceof APICreateVpnRouteMsg) {
            handle((APICreateVpnRouteMsg) msg);
        } else if (msg instanceof APIDeleteVpnRouteMsg) {
            handle((APIDeleteVpnRouteMsg) msg);
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
    private void handle(APICreateVpnGatewayMsg msg) {
        VpnGatewayVO gateway = new VpnGatewayVO();
        gateway.setExpiredDate(Timestamp.valueOf(LocalDateTime.now().plus(msg.getMonths(), ChronoUnit.MONTHS)));
    }

    private void handle(APIUpdateVpnGatewayMsg msg) {

    }

    private void handle(APIUpdateVpnBindwidthMsg msg) {

    }
    private void handle(APIDeleteVpnGatewayMsg msg) {

    }
    private void handle(APICreateTunnelIfaceMsg msg) {

    }

    private void handle(APIUpdateTunnelIfaceMsg msg) {

    }
    private void handle(APIDeleteTunnelIfaceMsg msg) {

    }

    private void handle(APICreateVpnRouteMsg msg) {

    }
    private void handle(APIDeleteVpnRouteMsg msg) {

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
        } else if (msg instanceof APICreateVpnGatewayMsg) {
            validate((APICreateVpnGatewayMsg) msg);
        } else if (msg instanceof APIDeleteVpnGatewayMsg) {
            validate((APIDeleteVpnGatewayMsg) msg);
        } else if (msg instanceof APIQueryVpnGatewayMsg) {
            validate((APIQueryVpnGatewayMsg) msg);
        } else if (msg instanceof APIUpdateVpnGatewayMsg) {
            validate((APIUpdateVpnGatewayMsg) msg);
        } else if (msg instanceof APIUpdateVpnBindwidthMsg) {
            validate((APIUpdateVpnBindwidthMsg) msg);
        } else if (msg instanceof APIQueryTunnelIfaceMsg) {
            validate((APIQueryTunnelIfaceMsg) msg);
        } else if (msg instanceof APIUpdateTunnelIfaceMsg) {
            validate((APIUpdateTunnelIfaceMsg) msg);
        } else if (msg instanceof APIDeleteTunnelIfaceMsg) {
            validate((APIDeleteTunnelIfaceMsg) msg);
        } else if (msg instanceof APICreateTunnelIfaceMsg) {
            validate((APICreateTunnelIfaceMsg) msg);
        } else if (msg instanceof APICreateVpnRouteMsg) {
            validate((APICreateVpnRouteMsg) msg);
        } else if (msg instanceof APIDeleteVpnRouteMsg) {
            validate((APIDeleteVpnRouteMsg) msg);
        }
        return msg;
    }

    private void validate(APIDeleteVpnRouteMsg msg) {

    }

    private void validate(APICreateVpnRouteMsg msg) {

    }

    private void validate(APICreateTunnelIfaceMsg msg) {

    }

    private void validate(APIDeleteTunnelIfaceMsg msg) {

    }

    private void validate(APIUpdateTunnelIfaceMsg msg) {

    }

    private void validate(APIQueryTunnelIfaceMsg msg) {

    }

    private void validate(APIUpdateVpnBindwidthMsg msg) {

    }

    private void validate(APIUpdateVpnGatewayMsg msg) {

    }

    private void validate(APIQueryVpnGatewayMsg msg) {

    }

    private void validate(APIDeleteVpnGatewayMsg msg) {

    }

    private void validate(APICreateVpnGatewayMsg msg) {
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
