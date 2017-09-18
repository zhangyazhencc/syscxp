package org.zstack.vpn.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.vpn.header.gateway.*;
import org.zstack.vpn.header.host.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.zstack.core.Platform.argerr;

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
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIDeleteVpnHostMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), VpnHostVO.class);
        APIDeleteVpnHostEvent evt = new APIDeleteVpnHostEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIUpdateVpnHostMsg msg) {
        VpnHostVO host = dbf.findByUuid(msg.getUuid(), VpnHostVO.class);
        if (StringUtils.isEmpty(msg.getName())) ;

    }

    private void handle(APICreateVpnHostMsg msg) {
        VpnHostVO host = new VpnHostVO();
        host.setUuid(Platform.getUuid());
        host.setName(msg.getName());
        host.setDescription(msg.getDescription());
        host.setPublicIface(msg.getPublicIface());
        host.setTunnelIface(msg.getTunnelIface());
        host.setHostIp(msg.getHostIp());
        host.setSshPort(msg.getSshPort());
        host.setUsername(msg.getUsername());
        host.setPassword(msg.getPassword());
        host.setState(HostState.Enabled);
        host.setStatus(HostStatus.Connecting);

        host = dbf.persistAndRefresh(host);
        APICreateVpnHostEvent evt = new APICreateVpnHostEvent(msg.getId());
        evt.setInventory(VpnHostInventory.valueOf(host));
        bus.publish(evt);
    }

    private void handle(APICreateVpnGatewayMsg msg) {
        VpnGatewayVO gateway = new VpnGatewayVO();
        gateway.setUuid(Platform.getUuid());
        gateway.setAccountUuid(msg.getSession().getAccountUuid());
        gateway.setHostUuid(msg.getHostUuid());
        gateway.setName(msg.getDescription());
        gateway.setVpnCidr(msg.getVpnCidr());
        gateway.setBandwidth(msg.getBandwidth());
        gateway.setEndpointUuid(msg.getEndpointUuid());
        gateway.setStatus(VpnStatus.STOP);
        gateway.setMonths(msg.getMonths());
        gateway.setExpiredDate(Timestamp.valueOf(LocalDateTime.now()
                .plus(msg.getMonths(), ChronoUnit.MONTHS)));

        // Todo create order
        gateway = dbf.persistAndRefresh(gateway);
        APICreateVpnGatewayEvent evt = new APICreateVpnGatewayEvent(msg.getId());
        evt.setInventory(VpnGatewayInventory.valueOf(gateway));
        bus.publish(evt);
    }

    private void handle(APIUpdateVpnGatewayMsg msg) {
        VpnGatewayVO gateway = dbf.findByUuid(msg.getUuid(), VpnGatewayVO.class);
        boolean update = false;
        if (StringUtils.isEmpty(msg.getName())) {
            gateway.setName(msg.getName());
            update = true;
        }
        if (StringUtils.isEmpty(msg.getDescription())) {
            gateway.setDescription(msg.getDescription());
            update = true;
        }
        if (StringUtils.isEmpty(msg.getVpnCidr())) {
            gateway.setVpnCidr(msg.getVpnCidr());
            update = true;
        }
        if (msg.getStatus() != null) {
            gateway.setStatus(msg.getStatus());
            update = true;
        }
        dbf.updateAndRefresh(gateway);
        APIUpdateVpnGatewayEvent evt = new APIUpdateVpnGatewayEvent(msg.getId());
        evt.setInventory(VpnGatewayInventory.valueOf(gateway));
        bus.publish(evt);
    }

    private void handle(APIUpdateVpnBindwidthMsg msg) {
        VpnGatewayVO gateway = dbf.findByUuid(msg.getUuid(), VpnGatewayVO.class);
        gateway.setBandwidth(msg.getBandwidth());

        // Todo create order
        gateway = dbf.persistAndRefresh(gateway);
        APIUpdateVpnGatewayEvent evt = new APIUpdateVpnGatewayEvent(msg.getId());
        evt.setInventory(VpnGatewayInventory.valueOf(gateway));
        bus.publish(evt);
    }

    private void handle(APIDeleteVpnGatewayMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), VpnGatewayVO.class);
        APIDeleteVpnHostEvent evt = new APIDeleteVpnHostEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APICreateTunnelIfaceMsg msg) {
        TunnelIfaceVO tunnelIface = new TunnelIfaceVO();
        tunnelIface.setUuid(Platform.getUuid());
        tunnelIface.setGatewayUuid(msg.getGatewayUuid());
        tunnelIface.setName(msg.getName());
        tunnelIface.setDescription(msg.getDescription());
        tunnelIface.setTunnelUuid(msg.getTunnelUuid());
        tunnelIface.setTunnelName(msg.getTunnelName());
        tunnelIface.setServerIP(msg.getServerIP());
        tunnelIface.setClientIP(msg.getClientIP());
        tunnelIface.setMask(msg.getMask());

        tunnelIface = dbf.persistAndRefresh(tunnelIface);
        APICreateTunnelIfaceEvent evt = new APICreateTunnelIfaceEvent(msg.getId());
        evt.setInventory(TunnelIfaceInventory.valueOf(tunnelIface));
        bus.publish(evt);
    }

    private void handle(APIUpdateTunnelIfaceMsg msg) {
        TunnelIfaceVO tunnelIface = dbf.findByUuid(msg.getUuid(), TunnelIfaceVO.class);
        tunnelIface.setName(msg.getName());
        tunnelIface = dbf.persistAndRefresh(tunnelIface);
        APIUpdateTunnelIfaceEvent evt = new APIUpdateTunnelIfaceEvent(msg.getId());
        evt.setInventory(TunnelIfaceInventory.valueOf(tunnelIface));
        bus.publish(evt);
    }

    private void handle(APIDeleteTunnelIfaceMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), VpnGatewayVO.class);
        APIDeleteTunnelIfaceEvent evt = new APIDeleteTunnelIfaceEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APICreateVpnRouteMsg msg) {
        VpnRouteVO vpnRoute = new VpnRouteVO();
        vpnRoute.setGatewayUuid(msg.getGatewayUuid());
        vpnRoute.setRouteType(msg.getRouteType());
        vpnRoute.setNextIfaceUuid(msg.getNextIfaceUuid());
        vpnRoute.setNextIfaceName(msg.getNextIfaceName());
        vpnRoute.setTargetCidr(msg.getTargetCidr());

        vpnRoute = dbf.persistAndRefresh(vpnRoute);
        APICreateVpnRouteEvent evt = new APICreateVpnRouteEvent(msg.getId());
        evt.setInventory(VpnRouteInventory.valueOf(vpnRoute));
        bus.publish(evt);
    }

    private void handle(APIDeleteVpnRouteMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), VpnGatewayVO.class);
        APIDeleteVpnRouteEvent evt = new APIDeleteVpnRouteEvent(msg.getId());
        bus.publish(evt);
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
        SimpleQuery query = dbf.createQuery(VpnHostVO.class);
        query.add(VpnHostVO_.name, SimpleQuery.Op.EQ, msg.getName());
        if (query.isExists()) {
            throw new ApiMessageInterceptionException(argerr(
                    "The VpnHost[name:%s] is already exist.", msg.getName()
            ));
        }
    }

    private void validate(APIUpdateVpnHostMsg msg) {
    }

    private void validate(APIDeleteVpnHostMsg msg) {
    }

    private void validate(APIQueryVpnHostMsg msg) {
    }

}