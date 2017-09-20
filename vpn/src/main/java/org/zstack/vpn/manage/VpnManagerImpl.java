package org.zstack.vpn.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.Constants;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.core.ReturnValueCompletion;
import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.header.rest.JsonAsyncRESTCallback;
import org.zstack.header.rest.RESTFacade;
import org.zstack.vpn.header.host.VpnState;
import org.zstack.vpn.header.host.VpnStatus;
import org.zstack.vpn.header.vpn.*;
import org.zstack.vpn.manage.VpnCommands.*;
import org.zstack.vpn.header.host.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.zstack.core.Platform.argerr;

public class VpnManagerImpl extends AbstractService implements VpnManager, ApiMessageInterceptor {

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
        } else if (msg instanceof APICreateVpnMsg) {
            handle((APICreateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnMsg) {
            handle((APIUpdateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnBindwidthMsg) {
            handle((APIUpdateVpnBindwidthMsg) msg);
        } else if (msg instanceof APIDeleteVpnMsg) {
            handle((APIDeleteVpnMsg) msg);
        } else if (msg instanceof APICreateVpnInterfaceMsg) {
            handle((APICreateVpnInterfaceMsg) msg);
        } else if (msg instanceof APIUpdateVpnInterfaceMsg) {
            handle((APIUpdateVpnInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteVpnInterfaceMsg) {
            handle((APIDeleteVpnInterfaceMsg) msg);
        } else if (msg instanceof APICreateVpnRouteMsg) {
            handle((APICreateVpnRouteMsg) msg);
        } else if (msg instanceof APIDeleteVpnRouteMsg) {
            handle((APIDeleteVpnRouteMsg) msg);
        } else if (msg instanceof APIGetVpnMsg) {
            handle((APIGetVpnMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetVpnMsg msg) {
        VpnVO vpn = dbf.findByUuid(msg.getUuid(), VpnVO.class);

        SimpleQuery query = dbf.createQuery(VpnInterfaceVO.class);
        query.add(VpnInterfaceVO_.vpnUuid, SimpleQuery.Op.EQ, msg.getUuid());
        vpn.setVpnInterfaces(query.list());

        query = dbf.createQuery(VpnRouteVO.class);
        query.add(VpnRouteVO_.vpnUuid, SimpleQuery.Op.EQ, msg.getUuid());
        vpn.setVpnRoutes(query.list());

        APIGetVpnReply reply = new APIGetVpnReply();
        reply.setInventory(VpnInventory.valueOf(vpn));

        bus.reply(msg, reply);
    }

    private void handle(APIDeleteVpnHostMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), VpnHostVO.class);
        APIDeleteVpnHostEvent evt = new APIDeleteVpnHostEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIUpdateVpnHostMsg msg) {
        VpnHostVO host = dbf.findByUuid(msg.getUuid(), VpnHostVO.class);
        boolean update = false;
        if (StringUtils.isEmpty(msg.getName())) {
            host.setName(msg.getName());
            update = true;
        }
        if (StringUtils.isEmpty(msg.getDescription())) {
            host.setDescription(msg.getDescription());
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
        host.setPublicInterface(msg.getPublicIface());
        host.setTunnelInterface(msg.getTunnelIface());
        host.setManageIp(msg.getManageIp());
        host.setSshPort(msg.getSshPort());
        host.setUsername(msg.getUsername());
        host.setPassword(msg.getPassword());
        host.setState(VpnState.Enabled);
        host.setStatus(VpnStatus.Connecting);

        host = dbf.persistAndRefresh(host);
        APICreateVpnHostEvent evt = new APICreateVpnHostEvent(msg.getId());
        evt.setInventory(VpnHostInventory.valueOf(host));
        bus.publish(evt);
    }

    private void handle(APICreateVpnMsg msg) {
        VpnVO gateway = new VpnVO();
        gateway.setUuid(Platform.getUuid());
        gateway.setAccountUuid(msg.getSession().getAccountUuid());
        gateway.setHostUuid(msg.getHostUuid());
        gateway.setName(msg.getDescription());
        gateway.setVpnCidr(msg.getVpnCidr());
        gateway.setBandwidth(msg.getBandwidth());
        gateway.setEndpointUuid(msg.getEndpoint());
        gateway.setState(VpnState.Enabled);
        gateway.setStatus(VpnStatus.Connecting);
        gateway.setMonths(msg.getMonths());
        gateway.setExpiredDate(Timestamp.valueOf(LocalDateTime.now()
                .plus(msg.getMonths(), ChronoUnit.MONTHS)));

        // Todo create order
        gateway = dbf.persistAndRefresh(gateway);
        APICreateVpnEvent evt = new APICreateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(gateway));
        bus.publish(evt);
    }

    private void handle(APIUpdateVpnMsg msg) {
        VpnVO gateway = dbf.findByUuid(msg.getUuid(), VpnVO.class);
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
        if (msg.getState() != null) {
            gateway.setState(msg.getState());
            update = true;
        }
        if (update)
            dbf.updateAndRefresh(gateway);
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(gateway));
        bus.publish(evt);
    }

    private void handle(APIUpdateVpnBindwidthMsg msg) {
        VpnVO gateway = dbf.findByUuid(msg.getUuid(), VpnVO.class);
        gateway.setBandwidth(msg.getBandwidth());

        // Todo create
        gateway = dbf.persistAndRefresh(gateway);
        APIUpdateVpnEvent evt = new APIUpdateVpnEvent(msg.getId());
        evt.setInventory(VpnInventory.valueOf(gateway));
        bus.publish(evt);
    }

    private void handle(APIDeleteVpnMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), VpnVO.class);
        APIDeleteVpnHostEvent evt = new APIDeleteVpnHostEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APICreateVpnInterfaceMsg msg) {
        VpnInterfaceVO tunnelIface = new VpnInterfaceVO();
        tunnelIface.setUuid(Platform.getUuid());
        tunnelIface.setVpnUuid(msg.getGatewayUuid());
        tunnelIface.setName(msg.getName());
        tunnelIface.setTunnelUuid(msg.getTunnel());
        tunnelIface.setLocalIp(msg.getServerIP());
        tunnelIface.setRemoteIp(msg.getClientIP());
        tunnelIface.setNetmask(msg.getMask());

        tunnelIface = dbf.persistAndRefresh(tunnelIface);
        APICreateVpnInterfaceEvent evt = new APICreateVpnInterfaceEvent(msg.getId());
        evt.setInventory(VpnInterfaceInventory.valueOf(tunnelIface));
        bus.publish(evt);
    }

    private void handle(APIUpdateVpnInterfaceMsg msg) {
        VpnInterfaceVO tunnelIface = dbf.findByUuid(msg.getUuid(), VpnInterfaceVO.class);
        tunnelIface.setName(msg.getName());
        tunnelIface = dbf.persistAndRefresh(tunnelIface);
        APIUpdateVpnInterfaceEvent evt = new APIUpdateVpnInterfaceEvent(msg.getId());
        evt.setInventory(VpnInterfaceInventory.valueOf(tunnelIface));
        bus.publish(evt);
    }

    private void handle(APIDeleteVpnInterfaceMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), VpnVO.class);
        APIDeleteVpnInterfaceEvent evt = new APIDeleteVpnInterfaceEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APICreateVpnRouteMsg msg) {
        VpnRouteVO vpnRoute = new VpnRouteVO();
        vpnRoute.setVpnUuid(msg.getGatewayUuid());
        vpnRoute.setRouteType(msg.getRouteType());
        vpnRoute.setNextInterface(msg.getNextIface());
        vpnRoute.setTargetCidr(msg.getTargetCidr());

        vpnRoute = dbf.persistAndRefresh(vpnRoute);
        APICreateVpnRouteEvent evt = new APICreateVpnRouteEvent(msg.getId());
        evt.setInventory(VpnRouteInventory.valueOf(vpnRoute));
        bus.publish(evt);
    }

    private void handle(APIDeleteVpnRouteMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), VpnVO.class);
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
        } else if (msg instanceof APICreateVpnMsg) {
            validate((APICreateVpnMsg) msg);
        } else if (msg instanceof APIDeleteVpnMsg) {
            validate((APIDeleteVpnMsg) msg);
        } else if (msg instanceof APIQueryVpnMsg) {
            validate((APIQueryVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnMsg) {
            validate((APIUpdateVpnMsg) msg);
        } else if (msg instanceof APIUpdateVpnBindwidthMsg) {
            validate((APIUpdateVpnBindwidthMsg) msg);
        } else if (msg instanceof APIUpdateVpnInterfaceMsg) {
            validate((APIUpdateVpnInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteVpnInterfaceMsg) {
            validate((APIDeleteVpnInterfaceMsg) msg);
        } else if (msg instanceof APICreateVpnInterfaceMsg) {
            validate((APICreateVpnInterfaceMsg) msg);
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

    private void validate(APICreateVpnInterfaceMsg msg) {
    }

    private void validate(APIDeleteVpnInterfaceMsg msg) {
    }

    private void validate(APIUpdateVpnInterfaceMsg msg) {
    }

    private void validate(APIUpdateVpnBindwidthMsg msg) {
    }

    private void validate(APIUpdateVpnMsg msg) {
    }

    private void validate(APIQueryVpnMsg msg) {
    }

    private void validate(APIDeleteVpnMsg msg) {
    }

    private void validate(APICreateVpnMsg msg) {
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

    class Http<T> {
        String path;
        AgentCommand cmd;
        Class<T> responseClass;

        public Http(String path, AgentCommand cmd, Class<T> rspClz) {
            this.path = path;
            this.cmd = cmd;
            this.responseClass = rspClz;
        }

        void call(ReturnValueCompletion<T> completion) {
            Map<String, String> header = new HashMap<>();
            header.put(VpnConstant.HTTP_HEADER_VPN_UUID, cmd.getVpnUuid());
            header.put(VpnConstant.HTTP_HEADER_VPN_HOST_IP, cmd.getHostIp());
            restf.asyncJsonPost(path, cmd, header, new JsonAsyncRESTCallback<T>(completion) {
                @Override
                public void fail(ErrorCode err) {
                    completion.fail(err);
                }

                @Override
                public void success(T ret) {
                    completion.success(ret);
                }

                @Override
                public Class<T> getReturnClass() {
                    return responseClass;
                }
            });
        }
    }
}