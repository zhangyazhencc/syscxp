package org.zstack.vpn.host;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.Q;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.header.rest.RESTFacade;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;
import org.zstack.vpn.header.host.*;
import org.zstack.vpn.vpn.VpnCommands.*;
import org.zstack.vpn.vpn.VpnRESTCaller;

import static org.zstack.core.Platform.argerr;


public class HostManagerImpl extends AbstractService implements HostManager, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(HostManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;
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
        } else if (msg instanceof APICreateHostInterfaceMsg) {
            handle((APICreateHostInterfaceMsg) msg);
        } else if (msg instanceof APICreateZoneMsg) {
            handle((APICreateZoneMsg) msg);
        } else if (msg instanceof APIDeleteHostInterfaceMsg) {
            handle((APIDeleteHostInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteZoneMsg) {
            handle((APIDeleteZoneMsg) msg);
        } else if (msg instanceof APIUpdateVpnHostStateMsg) {
            handle((APIUpdateVpnHostStateMsg) msg);
        } else if (msg instanceof APIUpdateZoneMsg) {
            handle((APIUpdateZoneMsg) msg);
        } else if (msg instanceof APIReconnectVpnHostMsg){
            handle((APIReconnectVpnHostMsg) msg);
        }else {
            bus.dealWithUnknownMessage(msg);
        }
    }



    private void handle(APIReconnectVpnHostMsg msg) {
        VpnHostVO host = dbf.findByUuid(msg.getUuid(), VpnHostVO.class);

        ReconnectVpnHostCmd cmd = ReconnectVpnHostCmd.valueOf(host.getUuid());
        new VpnRESTCaller().syncPostForVPN(HostConstant.RECONNECT_HOST__PATH, cmd, ReconnectVpnHostResponse.class);

        APIReconnectVpnHostEvent evt = new APIReconnectVpnHostEvent(msg.getId());
        bus.publish(evt);
    }


    private void handle(APIDeleteHostInterfaceMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), HostInterfaceVO.class);
        APIDeleteHostInterfaceEvent evt = new APIDeleteHostInterfaceEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIDeleteZoneMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), ZoneVO.class);
        APIDeleteZoneEvent evt = new APIDeleteZoneEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIUpdateZoneMsg msg) {
        ZoneVO zone = dbf.findByUuid(msg.getUuid(), ZoneVO.class);
        boolean update = false;
        if (!StringUtils.isEmpty(msg.getName())) {
            zone.setName(msg.getName());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getDescription())) {
            zone.setDescription(msg.getDescription());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getNodeUuid())) {
            zone.setNodeUuid(msg.getNodeUuid());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getProvince())) {
            zone.setProvince(msg.getProvince());
            update = true;
        }
        if (update)
            zone = dbf.updateAndRefresh(zone);

        APIUpdateZoneEvent evt = new APIUpdateZoneEvent(msg.getId());
        evt.setInventory(ZoneInventory.valueOf(zone));
        bus.publish(evt);

    }

    private void handle(APIUpdateVpnHostStateMsg msg) {
        VpnHostVO host = dbf.findByUuid(msg.getUuid(), VpnHostVO.class);

        host.setState(HostState.valueOf(msg.getState()));

        host = dbf.updateAndRefresh(host);
        APIUpdateVpnHostStateEvent evt = new APIUpdateVpnHostStateEvent(msg.getId());
        evt.setInventory(VpnHostInventory.valueOf(host));
        bus.publish(evt);
    }

    private void handle(APICreateZoneMsg msg) {
        ZoneVO zone = new ZoneVO();
        zone.setUuid(Platform.getUuid());
        zone.setProvince(msg.getProvince());
        zone.setName(msg.getName());
        zone.setDescription(msg.getDescription());
        zone.setNodeUuid(msg.getNodeUuid());

        zone = dbf.persistAndRefresh(zone);
        APICreateZoneEvent evt = new APICreateZoneEvent(msg.getId());
        evt.setInventory(ZoneInventory.valueOf(zone));
        bus.publish(evt);
    }

    private void handle(APICreateHostInterfaceMsg msg) {
        HostInterfaceVO iface = new HostInterfaceVO();
        iface.setUuid(Platform.getUuid());
        iface.setName(msg.getName());
        iface.setHostUuid(msg.getHostUuid());
        iface.setEndpointUuid(msg.getEndpointUuid());
        iface.setInterfaceUuid(msg.getInterfaceUuid());

        iface = dbf.persistAndRefresh(iface);

        APICreateHostInterfaceEvent evt = new APICreateHostInterfaceEvent(msg.getId());
        evt.setInventory(HostInterfaceInventory.valueOf(iface));
        bus.publish(evt);
    }


    private void handle(APIDeleteVpnHostMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), VpnHostVO.class);

        APIDeleteVpnHostEvent evt = new APIDeleteVpnHostEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIUpdateVpnHostMsg msg) {
        VpnHostVO host = dbf.findByUuid(msg.getUuid(), VpnHostVO.class);
        boolean update = false;
        if (!StringUtils.isEmpty(msg.getName())) {
            host.setName(msg.getName());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getDescription())) {
            host.setDescription(msg.getDescription());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getPublicInterface())) {
            host.setPublicInterface(msg.getPublicInterface());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getPublicIp())) {
            host.setPublicIp(msg.getPublicIp());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getManageIp())) {
            host.setManageIp(msg.getManageIp());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getSshPort())) {
            host.setSshPort(msg.getSshPort());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getUsername())) {
            host.setUsername(msg.getUsername());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getPassword())) {
            host.setPassword(msg.getPassword());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getZoneUuid())) {
            host.setZone(dbf.findByUuid(msg.getZoneUuid(), ZoneVO.class));
            update = true;
        }

        if (update)
            dbf.updateAndRefresh(host);

        APIUpdateVpnHostEvent evt = new APIUpdateVpnHostEvent(msg.getId());
        evt.setInventory(VpnHostInventory.valueOf(host));
        bus.publish(evt);
    }

    private void handle(APICreateVpnHostMsg msg) {
        VpnHostVO host = new VpnHostVO();
        host.setUuid(Platform.getUuid());
        host.setName(msg.getName());
        host.setDescription(msg.getDescription());
        host.setPublicInterface(msg.getPublicInterface());
        host.setPublicIp(msg.getPublicIp());
        host.setZone(dbf.findByUuid(msg.getZoneUuid(), ZoneVO.class));
        host.setManageIp(msg.getManageIp());
        host.setSshPort(msg.getSshPort());
        host.setUsername(msg.getUsername());
        host.setPassword(msg.getPassword());
        host.setState(HostState.Enabled);
        host.setStatus(HostStatus.Connecting);


        AddVpnHostCmd cmd = AddVpnHostCmd.valueOf(host.getUuid());
        new VpnRESTCaller().syncPostForVPN(HostConstant.ADD_HOST__PATH, cmd, UpdateVpnBandWidthResponse.class);


        host = dbf.persistAndRefresh(host);
        APICreateVpnHostEvent evt = new APICreateVpnHostEvent(msg.getId());
        evt.setInventory(VpnHostInventory.valueOf(host));
        bus.publish(evt);
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }


    public String getId() {
        return bus.makeLocalServiceId(HostConstant.SERVICE_ID);
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
        } else if (msg instanceof APICreateZoneMsg) {
            validate((APICreateZoneMsg) msg);
        } else if (msg instanceof APIDeleteZoneMsg) {
            validate((APIDeleteZoneMsg) msg);
        } else if (msg instanceof APIDeleteHostInterfaceMsg) {
            validate((APIDeleteHostInterfaceMsg) msg);
        }
        return msg;
    }


    private void validate(APIDeleteHostInterfaceMsg msg) {
    }

    private void validate(APIDeleteZoneMsg msg) {
        Q q = Q.New(VpnHostVO.class)
                .eq(VpnHostVO_.zoneUuid, msg.getUuid());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The Zone[uuid:%s] has at least one vpn host, can not delete.", msg.getUuid()
            ));
    }

    private void validate(APICreateZoneMsg msg) {
        Q q = Q.New(ZoneVO.class)
                .eq(ZoneVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The ZoneVO[name:%s] is already exist.", msg.getName()
            ));
    }


    private void validate(APICreateVpnHostMsg msg) {
        Q q = Q.New(VpnHostVO.class)
                .eq(VpnHostVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The VpnHost[name:%s] is already exist.", msg.getName()
            ));
    }

    private void validate(APIUpdateVpnHostMsg msg) {
        Q q = Q.New(VpnHostVO.class)
                .eq(VpnHostVO_.name, msg.getName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr(
                    "The VpnHost[name:%s] is already exist.", msg.getName()
            ));
    }


}