package com.syscxp.vpn.host;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.ansible.AnsibleFacade;
import com.syscxp.core.cloudbus.*;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.host.HostGlobalProperty;
import com.syscxp.core.thread.AsyncThread;
import com.syscxp.header.AbstractService;
import com.syscxp.header.Component;
import com.syscxp.header.host.*;
import com.syscxp.header.managementnode.ManagementNodeReadyExtensionPoint;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.message.NeedReplyMessage;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.header.vpn.host.*;
import com.syscxp.header.vpn.VpnConstant;
import com.syscxp.vpn.vpn.VpnGlobalProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

public class VpnHostFactory extends AbstractService implements HostFactory, Component,
        ManagementNodeReadyExtensionPoint {
    private static final CLogger logger = Utils.getLogger(VpnHostFactory.class);

    public static final HostType hostType = new HostType(VpnConstant.HOST_TYPE);
    private List<VpnHostConnectExtensionPoint> connectExtensions = new ArrayList<>();

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private AnsibleFacade asf;
    @Autowired
    private ResourceDestinationMaker destMaker;
    @Autowired
    private CloudBus bus;
    @Autowired
    private RESTFacade restf;

    @Override
    public HostVO createHost(HostVO vo, AddHostMessage msg) {
        APICreateVpnHostMsg amsg = (APICreateVpnHostMsg) msg;
        VpnHostVO host = new VpnHostVO(vo);
        host.setPublicInterface(amsg.getPublicInterface());
        host.setPublicIp(amsg.getPublicIp());
        host.setZoneUuid(amsg.getZoneUuid());
        host.setVpnInterfaceName(amsg.getVpnInterfaceName());
        host.setUsername(amsg.getUsername());
        host.setPassword(amsg.getPassword());
        host.setSshPort(amsg.getSshPort());
        host.setStartPort(VpnHostConstant.HOST_START_PORT);
        host.setStartPort(VpnHostConstant.HOST_START_PORT + 1000);
        return dbf.persistAndRefresh(host);
    }

    @Override
    public Host getHost(HostVO vo) {
        VpnHostVO host = dbf.findByUuid(vo.getUuid(), VpnHostVO.class);
        VpnHostContext context = getHostContext(vo.getUuid());
        if (context == null) {
            context = createHostContext(host);
        }
        return new VpnHost(host, context);
    }

    @Override
    public HostType getHostType() {
        return hostType;
    }

    private List<String> getHostManagedByUs() {
        int qun = 10000;
        long amount = dbf.count(HostVO.class);
        int times = (int) (amount / qun) + (amount % qun != 0 ? 1 : 0);
        List<String> hostUuids = new ArrayList<String>();
        int start = 0;
        for (int i = 0; i < times; i++) {
            SimpleQuery<HostVO> q = dbf.createQuery(HostVO.class);
            q.select(HostVO_.uuid);
            // disconnected host will be handled by HostManager
            q.add(HostVO_.status, SimpleQuery.Op.EQ, HostStatus.Connected);
            q.setLimit(qun);
            q.setStart(start);
            List<String> lst = q.listValue();
            start += qun;
            for (String huuid : lst) {
                if (!destMaker.isManagedByUs(huuid)) {
                    continue;
                }
                hostUuids.add(huuid);
            }
        }

        return hostUuids;
    }

    @Override
    public HostInventory getHostInventory(HostVO vo) {
        VpnHostVO host = vo instanceof VpnHostVO ? (VpnHostVO) vo : dbf.findByUuid(vo.getUuid(), VpnHostVO.class);
        return VpnHostInventory.valueOf(host);
    }

    @Override
    public HostInventory getHostInventory(String uuid) {
        VpnHostVO vo = dbf.findByUuid(uuid, VpnHostVO.class);
        return vo == null ? null : VpnHostInventory.valueOf(vo);
    }

    private void deployAnsibleModule() {
        if (CoreGlobalProperty.UNIT_TEST_ON) {
            return;
        }

        asf.deployModule(VpnHostConstant.ANSIBLE_MODULE_PATH, VpnHostConstant.ANSIBLE_PLAYBOOK_NAME);
    }

    private void populateExtensions() {
        connectExtensions = pluginRgty.getExtensionList(VpnHostConnectExtensionPoint.class);
    }

    public List<VpnHostConnectExtensionPoint> getConnectExtensions() {
        return connectExtensions;
    }

    @Override
    public boolean start() {
        deployAnsibleModule();
        populateExtensions();

        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    public VpnHostContext createHostContext(VpnHostVO vo) {
        UriComponentsBuilder ub = UriComponentsBuilder.newInstance();
        ub.scheme(VpnGlobalProperty.AGENT_URL_SCHEME);
        ub.host(vo.getHostIp());
        ub.port(VpnGlobalProperty.AGENT_PORT);
        if (!"".equals(VpnGlobalProperty.AGENT_URL_ROOT_PATH)) {
            ub.path(VpnGlobalProperty.AGENT_URL_ROOT_PATH);
        }
        String baseUrl = ub.build().toUriString();

        VpnHostContext context = new VpnHostContext();
        context.setInventory(VpnHostInventory.valueOf(vo));
        context.setBaseUrl(baseUrl);
        return context;
    }

    public VpnHostContext getHostContext(String hostUuid) {
        VpnHostVO host = dbf.findByUuid(hostUuid, VpnHostVO.class);
        return createHostContext(host);
    }

    @Override
    @AsyncThread
    public void managementNodeReady() {
        if (CoreGlobalProperty.UNIT_TEST_ON) {
            return;
        }

        if (!asf.isModuleChanged(VpnHostConstant.ANSIBLE_PLAYBOOK_NAME)) {
            return;
        }

        // hosts need to deploy new agent
        // connect hosts even if they are ConnectionState is Connected

        List<String> hostUuids = getHostManagedByUs();
        if (hostUuids.isEmpty()) {
            return;
        }

        logger.debug(String.format("need to connect hosts because host changed, uuids:%s", hostUuids));

        List<ConnectHostMsg> msgs = new ArrayList<>();
        for (String huuid : hostUuids) {
            ConnectHostMsg msg = new ConnectHostMsg();
            msg.setNewAdd(false);
            msg.setUuid(huuid);
            bus.makeLocalServiceId(msg, VpnConstant.SERVICE_ID);
            msgs.add(msg);
        }

        bus.send(msgs, HostGlobalProperty.HOST_LOAD_PARALLELISM_DEGREE, new CloudBusSteppingCallback
                (null) {
            @Override
            public void run(NeedReplyMessage msg, MessageReply reply) {
                ConnectHostMsg cmsg = (ConnectHostMsg) msg;
                if (!reply.isSuccess()) {
                    logger.warn(String.format("failed to connect host[uuid:%s], %s", cmsg.getHostUuid(), reply.getError()));
                } else {
                    logger.debug(String.format("successfully to connect kvm host[uuid:%s]", cmsg.getHostUuid()));
                }
            }
        });
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateHostInterfaceMsg) {
            handle((APICreateHostInterfaceMsg) msg);
        } else if (msg instanceof APICreateZoneMsg) {
            handle((APICreateZoneMsg) msg);
        } else if (msg instanceof APIDeleteHostInterfaceMsg) {
            handle((APIDeleteHostInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteZoneMsg) {
            handle((APIDeleteZoneMsg) msg);
        }  else if (msg instanceof APIUpdateZoneMsg) {
            handle((APIUpdateZoneMsg) msg);
        }  else if (msg instanceof APIUpdateVpnHostPortMsg) {
            handle((APIUpdateVpnHostPortMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }
    private void handle(APIUpdateVpnHostPortMsg msg) {
        APIUpdateVpnHostPortEvent evt = new APIUpdateVpnHostPortEvent(msg.getId());
        VpnHostVO host = dbf.findByUuid(msg.getUuid(), VpnHostVO.class);
        host.setStartPort(msg.getStartPort());
        host.setEndPort(msg.getEndPort());

        evt.setInventory(VpnHostInventory.valueOf(dbf.updateAndRefresh(host)));
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
        iface.setInterfaceName(msg.getInterfaceName());
        iface.setHostUuid(msg.getHostUuid());
        iface.setEndpointUuid(msg.getEndpointUuid());

        iface = dbf.persistAndRefresh(iface);

        APICreateHostInterfaceEvent evt = new APICreateHostInterfaceEvent(msg.getId());
        evt.setInventory(HostInterfaceInventory.valueOf(iface));
        bus.publish(evt);
    }




    @Override
    public String getId() {
        return bus.makeLocalServiceId(VpnHostConstant.SERVICE_ID);
    }
}
