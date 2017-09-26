package org.zstack.tunnel.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.cloudbus.ResourceDestinationMaker;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.tunnel.header.host.*;
import org.zstack.tunnel.header.monitor.*;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;
import org.zstack.utils.network.NetworkUtils;

import static org.zstack.core.Platform.argerr;

/**
 * Created by DCY on 2017-09-07
 */
public class MonitorManagerImpl extends AbstractService implements MonitorManager, ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(MonitorManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ResourceDestinationMaker destMaker;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private EventFacade evtf;


    @Override
    @MessageSafe
    public void handleMessage(Message msg) {

        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateHostMsg) {
            handle((APICreateHostMsg) msg);
        } else if (msg instanceof APIUpdateHostMsg) {
            handle((APIUpdateHostMsg) msg);
        } else if (msg instanceof APIDeleteHostMsg) {
            handle((APIDeleteHostMsg) msg);
        } else if (msg instanceof APICreateHostSwitchMonitorMsg) {
            handle((APICreateHostSwitchMonitorMsg) msg);
        } else if (msg instanceof APIUpdateHostSwitchMonitorMsg) {
            handle((APIUpdateHostSwitchMonitorMsg) msg);
        } else if (msg instanceof APIDeleteHostSwitchMonitorMsg) {
            handle((APIDeleteHostSwitchMonitorMsg) msg);
        } else if (msg instanceof APICreateTunnelMonitorMsg) {
            handle((APICreateTunnelMonitorMsg) msg);
        } else if (msg instanceof APIUpdateTunnelMonitorMsg) {
            handle((APIUpdateTunnelMonitorMsg) msg);
        } else if (msg instanceof APIDeleteTunnelMonitorMsg) {
            handle((APIDeleteTunnelMonitorMsg) msg);
        } else if (msg instanceof APICreateSpeedRecordsMsg) {
            handle((APICreateSpeedRecordsMsg) msg);
        } else if (msg instanceof APIUpdateSpeedRecordsMsg) {
            handle((APIUpdateSpeedRecordsMsg) msg);
        } else if (msg instanceof APICreateMonitorCidrMsg) {
            handle((APICreateMonitorCidrMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateHostMsg msg) {
        HostVO vo = new HostVO();

        vo.setUuid(Platform.getUuid());
        vo.setNodeUuid(msg.getNodeUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setHostIp(msg.getHostIp());
        vo.setUsername(msg.getUsername());
        vo.setPassword(msg.getPassword());
        vo.setState(HostState.Undeployed);
        vo.setStatus(HostStatus.Connected);

        vo = dbf.persistAndRefresh(vo);

        APICreateHostEvent evt = new APICreateHostEvent(msg.getId());
        evt.setInventory(HostInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateHostMsg msg) {
        HostVO vo = dbf.findByUuid(msg.getUuid(), HostVO.class);

        vo.setNodeUuid(msg.getNodeUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setHostIp(msg.getHostIp());
        vo.setUsername(msg.getUsername());
        vo.setPassword(msg.getPassword());
        vo.setState(msg.getState());
        vo.setStatus(msg.getStatus());

        vo = dbf.updateAndRefresh(vo);

        APIUpdateHostEvent evt = new APIUpdateHostEvent(msg.getId());
        evt.setInventory(HostInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteHostMsg msg) {
        String uuid = msg.getUuid();
        HostVO vo = dbf.findByUuid(uuid, HostVO.class);

        HostEO hostEO = dbf.findByUuid(uuid, HostEO.class);
        hostEO.setDeleted(1);
        dbf.update(hostEO);

        APIDeleteHostEvent event = new APIDeleteHostEvent(msg.getId());
        event.setInventory(HostInventory.valueOf(vo));

        bus.publish(event);
    }

    private void handle(APICreateHostSwitchMonitorMsg msg) {
        HostSwitchMonitorVO vo = new HostSwitchMonitorVO();

        vo.setUuid(Platform.getUuid());
        vo.setHostUuid(msg.getHostUuid());
        vo.setPhysicalSwitchUuid(msg.getPhysicalSwitchUuid());
        vo.setPhysicalSwitchPortName(msg.getPhysicalSwitchPortName());
        vo.setInterfaceName(msg.getInterfaceName());

        vo = dbf.persistAndRefresh(vo);

        APICreateHostSwitchMonitorEvent event = new APICreateHostSwitchMonitorEvent(msg.getId());
        event.setInventory(HostSwitchMonitorInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIUpdateHostSwitchMonitorMsg msg) {
        HostSwitchMonitorVO vo = dbf.findByUuid(msg.getUuid(), HostSwitchMonitorVO.class);

        vo.setHostUuid(msg.getHostUuid());
        vo.setPhysicalSwitchUuid(msg.getPhysicalSwitchUuid());
        vo.setPhysicalSwitchPortName(msg.getPhysicalSwitchPortName());
        vo.setInterfaceName(msg.getInterfaceName());
        vo = dbf.updateAndRefresh(vo);

        APIUpdateHostSwitchMonitorEvent event = new APIUpdateHostSwitchMonitorEvent(msg.getId());
        event.setInventory(HostSwitchMonitorInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIDeleteHostSwitchMonitorMsg msg) {
        String uuid = msg.getUuid();
        HostSwitchMonitorVO vo = dbf.findByUuid(uuid, HostSwitchMonitorVO.class);

        HostSwitchMonitorEO eo = dbf.findByUuid(uuid, HostSwitchMonitorEO.class);
        eo.setDeleted(1);
        dbf.update(eo);

        APIDeleteHostSwitchMonitorEvent event = new APIDeleteHostSwitchMonitorEvent(msg.getId());
        event.setInventory(HostSwitchMonitorInventory.valueOf(vo));

        bus.publish(event);
    }

    private void handle(APICreateTunnelMonitorMsg msg) {
        TunnelMonitorVO vo = new TunnelMonitorVO();

        vo.setUuid(Platform.getUuid());
        vo.setTunnelUuid(msg.getTunnelUuid());
        vo.setHostAUuid(msg.getHostAUuid());
        vo.setMonitorAIp(msg.getMonitorAIp());
        vo.setHostZUuid(msg.getHostZUuid());
        vo.setMonitorZIp(msg.getMonitorZIp());
        vo.setStatus(msg.getStatus());
        vo.setMsg(msg.getMsg());

        vo = dbf.persistAndRefresh(vo);

        APICreateTunnelMonitorEvent event = new APICreateTunnelMonitorEvent(msg.getId());
        event.setInventory(TunnelMonitorInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIUpdateTunnelMonitorMsg msg) {
        TunnelMonitorVO vo = dbf.findByUuid(msg.getUuid(), TunnelMonitorVO.class);

        vo.setTunnelUuid(msg.getTunnelUuid());
        vo.setHostAUuid(msg.getHostAUuid());
        vo.setMonitorAIp(msg.getMonitorAIp());
        vo.setHostZUuid(msg.getHostZUuid());
        vo.setMonitorZIp(msg.getMonitorZIp());
        vo.setStatus(msg.getStatus());
        if (msg.getMsg() != null) {
            vo.setMsg(msg.getMsg());
        }
        vo = dbf.updateAndRefresh(vo);

        APIUpdateTunnelMonitorEvent evt = new APIUpdateTunnelMonitorEvent(msg.getId());
        evt.setInventory(TunnelMonitorInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteTunnelMonitorMsg msg) {
        String uuid = msg.getUuid();
        TunnelMonitorVO vo = dbf.findByUuid(msg.getUuid(), TunnelMonitorVO.class);

        dbf.removeByPrimaryKey(uuid, TunnelMonitorVO.class);

        APIDeleteTunnelMonitorEvent event = new APIDeleteTunnelMonitorEvent(msg.getId());
        event.setInventory(TunnelMonitorInventory.valueOf(vo));

        bus.publish(event);
    }

    private void handle(APICreateSpeedRecordsMsg msg) {
        SpeedRecordsVO vo = new SpeedRecordsVO();

        vo.setUuid(Platform.getUuid());
        vo.setTunnelUuid(msg.getTunnelUuid());
        vo.setSrcHostUuid(msg.getSrcHostUuid());
        vo.setSrcMonitorIp(msg.getSrcMonitorIp());
        vo.setDstHostUuid(msg.getDstHostUuid());
        vo.setDstMonitorIp(msg.getDstMonitorIp());
        vo.setProtocolType(msg.getProtocolType());
        vo.setDuration(msg.getDuration());
        vo.setCompleted(0);
        vo = dbf.persistAndRefresh(vo);

        APICreateSpeedRecordsEvent event = new APICreateSpeedRecordsEvent(msg.getId());
        event.setInventory(SpeedRecordsInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIUpdateSpeedRecordsMsg msg) {
        SpeedRecordsVO vo = dbf.findByUuid(msg.getUuid(), SpeedRecordsVO.class);

        vo.setAvgSpeed(msg.getAvgSpeed());
        vo.setMaxSpeed(msg.getMaxSpeed());
        vo.setMinSpeed(msg.getMinSpeed());
        vo.setCompleted(msg.getCompleted());

        vo = dbf.updateAndRefresh(vo);

        APIUpdateSpeedRecordsEvent event = new APIUpdateSpeedRecordsEvent(msg.getId());
        event.setInventory(SpeedRecordsInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APICreateMonitorCidrMsg msg) {
        MonitorCidrVO vo = new MonitorCidrVO();

        vo.setUuid(Platform.getUuid());
        vo.setMonitorCidr(msg.getMonitorCidr());
        String startAddress = null;
        String endAddress = null;

        try {
            CIDRUtils cidrUtils = new CIDRUtils(msg.getMonitorCidr());
            startAddress = cidrUtils.getNetworkAddress();
            endAddress = cidrUtils.getBroadcastAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        vo.setStartAddress(startAddress);
        vo.setEndAddress(endAddress);

        vo = dbf.persistAndRefresh(vo);

        APICreateMonitorCidrEvent evt = new APICreateMonitorCidrEvent(msg.getId());
        evt.setInventory(MonitorCidrInventory.valueOf(vo));
        bus.publish(evt);
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
    public String getId() {
        return bus.makeLocalServiceId(MonitorConstant.SERVICE_ID);
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateHostMsg) {
            validate((APICreateHostMsg) msg);
        } else if (msg instanceof APIUpdateHostMsg) {
            validate((APIUpdateHostMsg) msg);
        } else if (msg instanceof APIDeleteHostMsg) {
            validate((APIDeleteHostMsg) msg);
        } else if (msg instanceof APICreateTunnelMonitorMsg) {
            validate((APICreateTunnelMonitorMsg) msg);
        } else if (msg instanceof APIUpdateTunnelMonitorMsg) {
            validate((APIUpdateTunnelMonitorMsg) msg);
        } else if (msg instanceof APICreateMonitorCidrMsg) {
            validate((APICreateMonitorCidrMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateMonitorCidrMsg msg) {
        //判断监控网段是否已经存在
        SimpleQuery<MonitorCidrVO> q = dbf.createQuery(MonitorCidrVO.class);
        q.add(MonitorCidrVO_.monitorCidr, SimpleQuery.Op.EQ, msg.getMonitorCidr());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr("MonitorCird %s is already exist ", msg.getMonitorCidr()));

    }

    private void validate(APICreateHostMsg msg) {
        //判断code是否已经存在
        SimpleQuery<HostVO> q = dbf.createQuery(HostVO.class);
        q.add(HostVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr("host's code %s is already exist ", msg.getCode()));

        //验证hostIp地址是否合法
        if (isPortEmpty(msg.getHostIp())) {
            msg.setHostIp(msg.getHostIp() + ":" + "22");
        }
        validateHostIp(msg.getHostIp());
    }

    private void validate(APIUpdateHostMsg msg) {
        //判断code是否已经存在
        if (msg.getCode() != null) {
            SimpleQuery<HostVO> queryHostCode = dbf.createQuery(HostVO.class);
            queryHostCode.add(HostVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            queryHostCode.add(HostVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (queryHostCode.isExists()) {
                throw new ApiMessageInterceptionException(argerr("host's code %s is already exist ", msg.getCode()));
            }
        }

        //验证hostIp地址是否合法
        if (isPortEmpty(msg.getHostIp())) {
            msg.setHostIp(msg.getHostIp() + ":" + "22");
        }
        validateHostIp(msg.getHostIp());
    }

    private void validate(APIDeleteHostMsg msg) {
        //判断host是否被HostSwitchMonitorVO关联
        SimpleQuery<HostSwitchMonitorVO> queryHostSwitch = dbf.createQuery(HostSwitchMonitorVO.class);
        queryHostSwitch.add(HostSwitchMonitorVO_.hostUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (queryHostSwitch.isExists())
            throw new ApiMessageInterceptionException(argerr("HostSwitchMonitor exist, cannot be deleted!"));

        //判断host是否被TunnelMonitorVO关联
        SimpleQuery<TunnelMonitorVO> queryTunnelMonitorA = dbf.createQuery(TunnelMonitorVO.class);
        queryTunnelMonitorA.add(TunnelMonitorVO_.hostAUuid, SimpleQuery.Op.EQ, msg.getUuid());

        SimpleQuery<TunnelMonitorVO> queryTunnelMonitorZ = dbf.createQuery(TunnelMonitorVO.class);
        queryTunnelMonitorZ.add(TunnelMonitorVO_.hostZUuid, SimpleQuery.Op.EQ, msg.getUuid());

        if (queryTunnelMonitorA.isExists() || queryTunnelMonitorZ.isExists())
            throw new ApiMessageInterceptionException(argerr("TunnelMonitor exist, cannot be deleted!"));
    }

    private void validate(APICreateTunnelMonitorMsg msg) {
        //验证monitorIp合法性
        if(!NetworkUtils.isIpv4Address(msg.getMonitorAIp()))
            throw new ApiMessageInterceptionException(argerr("Illegal monitor IP %s！", msg.getMonitorAIp()));

        if(!NetworkUtils.isIpv4Address(msg.getMonitorZIp()))
            throw new ApiMessageInterceptionException(argerr("Illegal monitor IP %s！", msg.getMonitorZIp()));
    }

    private void validate(APIUpdateTunnelMonitorMsg msg) {
        //验证monitorIp合法性
        if(!NetworkUtils.isIpv4Address(msg.getMonitorAIp()))
            throw new ApiMessageInterceptionException(argerr("Illegal monitor IP %s！", msg.getMonitorAIp()));

        if(!NetworkUtils.isIpv4Address(msg.getMonitorZIp()))
            throw new ApiMessageInterceptionException(argerr("Illegal monitor IP %s！", msg.getMonitorZIp()));
    }

    private void validateHostIp(String hostIp) {
        String ip = "";
        int port = -1;

        String[] hostAddress = hostIp.split(":");
        ip = hostAddress[0];
        try {
            port = Integer.parseInt(hostAddress[1]);
            if (!NetworkUtils.isLegalPort(port)) {
                throw new ApiMessageInterceptionException(argerr("Illegal host port %s！", port));
            }
        } catch (Exception e) {
            throw new ApiMessageInterceptionException(argerr("Illegal host port %s！", port));
        }

        if (!NetworkUtils.isIpv4Address(ip))
            throw new ApiMessageInterceptionException(argerr("Illegal host IP %s！", ip));
    }

    private boolean isPortEmpty(String hostIp) {
        if (!hostIp.contains(":"))
            return true;
        else
            return false;
    }
}
