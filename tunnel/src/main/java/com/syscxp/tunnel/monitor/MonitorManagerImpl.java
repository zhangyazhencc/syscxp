package com.syscxp.tunnel.monitor;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.db.*;
import com.syscxp.header.falconapi.FalconApiCommands;
import com.syscxp.header.falconapi.FalconApiRestConstant;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.host.APICreateMonitorHostMsg;
import com.syscxp.header.tunnel.host.MonitorHostVO;
import com.syscxp.header.tunnel.host.MonitorHostVO_;
import com.syscxp.header.tunnel.tunnel.TunnelMonitorState;
import com.syscxp.header.tunnel.monitor.*;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.TunnelSwitchPortVO;
import com.syscxp.header.tunnel.tunnel.TunnelSwitchPortVO_;
import com.syscxp.header.tunnel.tunnel.TunnelVO_;
import com.syscxp.tunnel.sdnController.ControllerCommands;
import com.syscxp.tunnel.sdnController.ControllerRestConstant;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.cloudbus.ResourceDestinationMaker;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.tunnel.TunnelVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.net.UnknownHostException;
import java.util.*;

import static com.syscxp.core.Platform.argerr;

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
    private RESTFacade evtf;



    public String getFalconServerUrl() {
        return String.format("http://%s:%s",
                CoreGlobalProperty.FALCON_API_IP, CoreGlobalProperty.FALCON_API_PORT);
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

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateHostSwitchMonitorMsg) {
            handle((APICreateHostSwitchMonitorMsg) msg);
        } else if (msg instanceof APIUpdateHostSwitchMonitorMsg) {
            handle((APIUpdateHostSwitchMonitorMsg) msg);
        } else if (msg instanceof APIDeleteHostSwitchMonitorMsg) {
            handle((APIDeleteHostSwitchMonitorMsg) msg);
        } else if (msg instanceof APIStartTunnelMonitorMsg) {
            handle((APIStartTunnelMonitorMsg) msg);
        } else if (msg instanceof APIRestartTunnelMonitorMsg) {
            handle((APIRestartTunnelMonitorMsg) msg);
        } else if (msg instanceof APIStopTunnelMonitorMsg) {
            handle((APIStopTunnelMonitorMsg) msg);
        } else if (msg instanceof APICreateSpeedRecordsMsg) {
            handle((APICreateSpeedRecordsMsg) msg);
        } else if (msg instanceof APIUpdateSpeedRecordsMsg) {
            handle((APIUpdateSpeedRecordsMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
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

        vo.setPhysicalSwitchUuid(msg.getPhysicalSwitchUuid());
        vo.setPhysicalSwitchPortName(msg.getPhysicalSwitchPortName());
        vo.setInterfaceName(msg.getInterfaceName());
        vo = dbf.updateAndRefresh(vo);

        APIUpdateHostSwitchMonitorEvent event = new APIUpdateHostSwitchMonitorEvent(msg.getId());
        event.setInventory(HostSwitchMonitorInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIDeleteHostSwitchMonitorMsg msg) {

        HostSwitchMonitorVO vo = dbf.findByUuid(msg.getUuid(), HostSwitchMonitorVO.class);

        dbf.remove(vo);

        APIDeleteHostSwitchMonitorEvent event = new APIDeleteHostSwitchMonitorEvent(msg.getId());
        event.setInventory(HostSwitchMonitorInventory.valueOf(vo));

        bus.publish(event);
    }

    @Transactional
    private void handle(APIStartTunnelMonitorMsg msg) {
        APIStartTunnelMonitorEvent event = new APIStartTunnelMonitorEvent(msg.getId());

        // 创建监控通道
        List<TunnelMonitorVO> tunnelMonitorVOS = createTunnelMonitorHandle(msg, event);

        // 控制器命令下发
        if (event.isSuccess())
            startControllerCommand(msg.getTunnelUuid(), tunnelMonitorVOS, event);

        // 同步icmp
        if (event.isSuccess())
            icmpSync(msg.getSession().getAccountUuid(), msg.getTunnelUuid(), tunnelMonitorVOS, event);

        // 更新tunnel状态
        if (event.isSuccess())
            updateTunnel(msg.getTunnelUuid(), msg.getMonitorCidr(), TunnelMonitorState.Enabled);
        else
            logger.error(String.format("tunnelUuid: %s 开启监控失败 Error: %s", msg.getTunnelUuid(), event.getError().toString()));

        bus.publish(event);
    }

    @Transactional
    private void handle(APIStopTunnelMonitorMsg msg) {
        APIStopTunnelMonitorEvent event = new APIStopTunnelMonitorEvent(msg.getId());

        stopTunnelMonitor(msg.getTunnelUuid(), event);

        bus.publish(event);
    }

    private void handle(APIRestartTunnelMonitorMsg msg) {
        APIRestartTunnelMonitorEvent event = new APIRestartTunnelMonitorEvent(msg.getId());

        // 获取监控通道数据
        List<TunnelMonitorVO> tunnelMonitorVOS = getTunnelMonitorByTunnel(msg.getTunnelUuid());

        // 控制器命令删除
        ControllerCommands.TunnelMonitorCommand cmd = getTunnelMonitorCommand(msg.getTunnelUuid(), tunnelMonitorVOS);
        stopControllerCommand(cmd, event);

        // 更新监控IP
        if (event.isSuccess()) {
            if (StringUtils.isNotEmpty(msg.getMonitorCidr())) {
                try {
                    CIDRUtils cidrUtils = new CIDRUtils(msg.getMonitorCidr());
                    String startIp = cidrUtils.getNetworkAddress();
                    String endIp = cidrUtils.getBroadcastAddress();

                    List<String> locatedIps =
                            Q.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelUuid, msg.getTunnelUuid()).select(TunnelMonitorVO_.monitorIp).findValue();

                    for (TunnelMonitorVO vo : tunnelMonitorVOS) {
                        vo.setMonitorIp(getMonitorIp(msg.getMonitorCidr(), startIp, endIp, locatedIps));
                        dbf.getEntityManager().persist(vo);
                    }
                } catch (UnknownHostException e) {
                    event.setSuccess(false);
                    event.setError(Platform.operr("获取监控ip错误！" + e.getMessage()));
                }
            }
        }

        // 控制器命令下发
        if (event.isSuccess())
            startControllerCommand(msg.getTunnelUuid(), tunnelMonitorVOS, event);

        // 同步icmp
        if (event.isSuccess())
            icmpSync(msg.getSession().getAccountUuid(), msg.getTunnelUuid(), tunnelMonitorVOS, event);

        // 更新tunnel状态
        if (event.isSuccess())
            updateTunnel(msg.getTunnelUuid(), msg.getMonitorCidr(), TunnelMonitorState.Enabled);
        else
            logger.error(String.format("tunnelUuid: %s 重启监控失败 Error: %s", msg.getTunnelUuid(), event.getError().toString()));

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

    /**
     * 创建监控通道 （创建完tunnel后手动开启）
     *
     * @param msg
     * @return：创建的监控通道
     */
    private List<TunnelMonitorVO> createTunnelMonitorHandle(APIStartTunnelMonitorMsg msg, APIEvent event) {
        List<TunnelMonitorVO> tunnelMonitorVOS = new ArrayList<TunnelMonitorVO>();
        try {
            // 按tunnel查找TunnelMonitorVO已经存在的IP
            List<String> locatedIps =
                    Q.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelUuid, msg.getTunnelUuid()).select(TunnelMonitorVO_.monitorIp).findValue();

            CIDRUtils cidrUtils = new CIDRUtils(msg.getMonitorCidr());
            String startIp = cidrUtils.getNetworkAddress();
            String endIp = cidrUtils.getBroadcastAddress();

            // 获取tunnel两端交换机端口
            List<TunnelSwitchPortVO> portVOS = getMonitorTunnelSwitchPortByTunnelId(msg.getTunnelUuid());
            for (TunnelSwitchPortVO tunnelSwitchPortVO : portVOS) {
                // 按tunnel port查询是否有已存在的监控机与监控IP
                TunnelMonitorVO monitorVO = new TunnelMonitorVO();
                if (monitorVO != null) {
                    tunnelMonitorVOS.add(monitorVO);
                    continue;
                }
                // 获取监控主机
                String hostUuid = getHostUuid(tunnelSwitchPortVO.getSwitchPortUuid());
                // 根据cidr获取监控IP
                String monitorIp = getMonitorIp(msg.getMonitorCidr(), startIp, endIp, locatedIps);

                monitorVO.setUuid(Platform.getUuid());
                monitorVO.setTunnelUuid(msg.getTunnelUuid());
                monitorVO.setTunnelSwitchPortUuid(tunnelSwitchPortVO.getUuid());
                monitorVO.setHostUuid(hostUuid);
                monitorVO.setMonitorIp(monitorIp);
                dbf.getEntityManager().persist(monitorVO);

                tunnelMonitorVOS.add(monitorVO);
            }
            dbf.getEntityManager().flush();
        } catch (Exception e) {
            event.setSuccess(false);
            event.setError(Platform.operr("tunnelUuid: %s %s Error: %s", msg.getTunnelUuid(), "创建监控通道失败！:" + e.getMessage()));
        }

        if (tunnelMonitorVOS.isEmpty()) {
            event.setSuccess(false);
            event.setError(Platform.operr("tunnelUuid: %s %s", msg.getTunnelUuid(), "创建监控通道失败！:"));
        }

        return tunnelMonitorVOS;
    }

    /***
     * 监控关闭、tunnel中止
     * @param tunnelUuid
     * @param event
     */
    public void stopTunnelMonitor(String tunnelUuid, APIEvent event) {
        // 获取监控通道数据
        List<TunnelMonitorVO> tunnelMonitorVOS = getTunnelMonitorByTunnel(tunnelUuid);

        // 控制器命令删除
        ControllerCommands.TunnelMonitorCommand cmd = getTunnelMonitorCommand(tunnelUuid, tunnelMonitorVOS);
        stopControllerCommand(cmd, event);

        // 删除icmp
        if (event.isSuccess())
            icmpDelete(tunnelUuid, event);

        // 更新tunnel状态
        if (event.isSuccess())
            updateTunnel(tunnelUuid, "", TunnelMonitorState.Disabled);
        else
            logger.error(String.format("tunnelUuid: %s 关闭监控失败 Error: %s", tunnelUuid, event.getError().toString()));
    }

    /***
     * 重启监控通道：tunnel修改vlan、调整带宽、修改端口调用
     * @param tunnelUuid
     * @param event
     */
    public void restartTunnelMonitor(String tunnelUuid, String accountUuid, ControllerCommands.TunnelMonitorCommand monitorCommand, APIEvent event) {
        // 控制器命令删除
        stopControllerCommand(monitorCommand, event);

        // 获取监控通道数据
        List<TunnelMonitorVO> tunnelMonitorVOS = getTunnelMonitorByTunnel(tunnelUuid);
        // 控制器命令下发
        if (event.isSuccess())
            startControllerCommand(tunnelUuid, tunnelMonitorVOS, event);

        // 同步icmp
        if (event.isSuccess())
            icmpSync(accountUuid, tunnelUuid, tunnelMonitorVOS, event);

        // 更新tunnel状态
        if (!event.isSuccess()) {
            updateTunnel(tunnelUuid, "", TunnelMonitorState.Disabled);
            logger.error("tunnelUuid:" + tunnelUuid + " 重新开启监控失败" + event.getError().toString());
        }
    }

    /***
     * 删除监控通道：删除tunnel
     * @param tunnelUuid
     * @param event
     */
    public void deleteTunnelMonitor(String tunnelUuid, APIEvent event) {
        // 获取监控通道数据
        List<TunnelMonitorVO> tunnelMonitorVOS = getTunnelMonitorByTunnel(tunnelUuid);

        // 控制器命令删除
        ControllerCommands.TunnelMonitorCommand cmd = getTunnelMonitorCommand(tunnelUuid, tunnelMonitorVOS);
        stopControllerCommand(cmd, event);

        // 删除icmp
        if (event.isSuccess())
            icmpDelete(tunnelUuid, event);

        // 删除监控通道数据
        UpdateQuery.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid).delete();

        // 更新tunnel状态
        if (event.isSuccess())
            updateTunnel(tunnelUuid,"",TunnelMonitorState.Disabled);
        else
            logger.error("tunnelUuid:" + tunnelUuid + " 关闭监控失败" + event.getError().toString());
    }

    /***
     * 更新tunnel监控状态
     * @param tunnelUuid
     * @param monitorCidr
     * @param monitorState
     */
    private void updateTunnel(String tunnelUuid, String monitorCidr, TunnelMonitorState monitorState) {
        // 更新tunnel状态
        TunnelVO tunnelVO = Q.New(TunnelVO.class)
                .eq(TunnelVO_.uuid, tunnelUuid)
                .find();

        tunnelVO.setMonitorState(monitorState);
        if (StringUtils.isNotEmpty(monitorCidr)) {
            tunnelVO.setMonitorCidr(monitorCidr);
        }
        dbf.getEntityManager().persist(tunnelVO);
    }

    /***
     * 监控命令下发至控制器
     * @param tunnelUuid
     * @param tunnelMonitorVOS
     * @return
     */
    private void startControllerCommand(String tunnelUuid, List<TunnelMonitorVO> tunnelMonitorVOS, APIEvent event) {
        String url = CoreGlobalProperty.CONTROLLER_MANAGER_URL;
        ControllerCommands.TunnelMonitorCommand cmd = getTunnelMonitorCommand(tunnelUuid, tunnelMonitorVOS);

        ControllerCommands.ControllerRestResponse response = new ControllerCommands.ControllerRestResponse();
        try {
            String command = JSONObjectUtil.toJsonString(cmd);
            response = evtf.syncJsonPost(url + ControllerRestConstant.START_TUNNEL_MONITOR, command, ControllerCommands.ControllerRestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        if (!response.isSuccess())
            event.setError(Platform.operr("监控命令下发失败！ %s", response.getMsg()));
    }

    /**
     * 控制器命令删除：关闭监控、中止tunnel
     *
     * @param event
     */
    private void stopControllerCommand(ControllerCommands.TunnelMonitorCommand cmd, APIEvent event) {
        String url = CoreGlobalProperty.CONTROLLER_MANAGER_URL;
        ControllerCommands.ControllerRestResponse response = new ControllerCommands.ControllerRestResponse();
        try {
            String command = JSONObjectUtil.toJsonString(cmd);
            response = evtf.syncJsonPost(url + ControllerRestConstant.STOP_TUNNEL_MONITOR, command, ControllerCommands.ControllerRestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        if (!response.isSuccess())
            event.setError(Platform.operr("监控命令删除失败！ %s", response.getMsg()));
    }

    /**
     * 开启监控、tunnel修改且监控为开启状态时同步ICMP到falcon_portal数据库
     *
     * @return：创建的监控通道
     */
    private void icmpSync(String accountUuid, String tunnelUuid, List<TunnelMonitorVO> tunnelMonitorVOS, APIEvent event) {
        String url = getFalconServerUrl();
        FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();
        try {
            FalconApiCommands.Icmp icmp = getIcmp(accountUuid, tunnelUuid, tunnelMonitorVOS);
            String savaJson = JSONObjectUtil.toJsonString(icmp);

            response = evtf.syncJsonPost(url + FalconApiRestConstant.ICMP_SYNC, savaJson, FalconApiCommands.RestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        if (!response.isSuccess())
            event.setError(Platform.operr("同步ICMP失败！ %s", response.getMsg()));
    }

    /***
     * 删除falcon_portal ICMP数据：关闭监控、tunnel删除、tunnel中止
     * @param tunnelUuid
     */
    private void icmpDelete(String tunnelUuid, APIEvent event) {
        String url = getFalconServerUrl();
        FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();

        try {
            FalconApiCommands falconApiCommands = new FalconApiCommands();
            falconApiCommands.setTunnel_id(tunnelUuid);
            String deleteJson = JSONObjectUtil.toJsonString(falconApiCommands);

            response = evtf.syncJsonPost(url + FalconApiRestConstant.ICMP_DELETE, deleteJson, FalconApiCommands.RestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        if (!response.isSuccess())
            event.setError(Platform.operr("删除ICMP失败！ %s", response.getMsg()));
    }

    public FalconApiCommands.Icmp getIcmp(String accountUuid, String tunnelUuid, List<TunnelMonitorVO> tunnelMonitorVOS) {
        FalconApiCommands.Icmp icmp = new FalconApiCommands.Icmp();
        TunnelVO tunnelVO = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).find();

        icmp.setTunnel_id(tunnelUuid);
        icmp.setTunnel_name(tunnelVO.getName());
        icmp.setUser_id(accountUuid);
        icmp.setCidr(tunnelVO.getMonitorCidr());

        for (TunnelMonitorVO tunnelMonitorVO : tunnelMonitorVOS) {
            TunnelSwitchPortVO tunnelSwitchPortVO = getTunnelSwitchPortByUuid(tunnelMonitorVO.getTunnelSwitchPortUuid());
            SwitchVO switchVO = getSwitchBySwitchPort(tunnelSwitchPortVO.getSwitchPortUuid());
            HostSwitchMonitorVO hostSwitchMonitorVO = getHostSwitchMonitorByHostUuid(tunnelMonitorVO.getHostUuid());

            if (InterfaceType.A.toString().equals(tunnelSwitchPortVO.getSortTag())) {
                icmp.setHostA_ip(tunnelMonitorVO.getMonitorIp());
                icmp.setEndpointA_mip(tunnelMonitorVO.getMonitorIp());
                icmp.setEndpointA_vid(tunnelSwitchPortVO.getVlan());
                icmp.setEndpointA_id(switchVO.getEndpointUuid());
                icmp.setEndpointA_ip(switchVO.getPhysicalSwitch().getmIP());
                icmp.setEndpointA_interface(hostSwitchMonitorVO.getInterfaceName());
            } else if (InterfaceType.Z.toString().equals(tunnelSwitchPortVO.getSortTag())) {
                icmp.setHostB_ip(tunnelMonitorVO.getMonitorIp());
                icmp.setEndpointB_mip(tunnelMonitorVO.getMonitorIp());
                icmp.setEndpointB_vid(tunnelSwitchPortVO.getVlan());
                icmp.setEndpointB_id(switchVO.getEndpointUuid());
                icmp.setEndpointB_ip(switchVO.getPhysicalSwitch().getmIP());
                icmp.setEndpointB_interface(hostSwitchMonitorVO.getInterfaceName());
            }
        }

        return icmp;
    }

    /**
     * 获取监控下发controller命令
     *
     * @param tunnelUuid
     * @return
     */
    public ControllerCommands.TunnelMonitorCommand getTunnelMonitorCommand(String tunnelUuid, List<TunnelMonitorVO> tunnelMonitorVOS) {
        List<ControllerCommands.TunnelMonitorMpls> mplsList = new ArrayList<>();
        List<ControllerCommands.TunnelMonitorSdn> sdnList = new ArrayList<>();

        Map<String, String> monitorIp = new HashMap<>();
        Map<String, String> monitorPort = new HashMap<>();

        getIpPort(tunnelMonitorVOS, monitorIp, monitorPort);

        TunnelVO tunnelVO = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).find();
        for (TunnelMonitorVO tunnelMonitorVO : tunnelMonitorVOS) {
            PhysicalSwitchVO physicalSwitchVO = getPhysicalSwitchByTunnelSwitchPort(tunnelMonitorVO.getTunnelSwitchPortUuid());
            TunnelSwitchPortVO tunnelSwitchPortVO = Q.New(TunnelSwitchPortVO.class).eq(TunnelSwitchPortVO_.uuid, tunnelMonitorVO.getTunnelSwitchPortUuid()).find();
            SwitchPortVO switchPortVO = Q.New(SwitchPortVO.class).eq(SwitchPortVO_.uuid, tunnelSwitchPortVO.getSwitchPortUuid()).find();

            ControllerCommands.TunnelMonitorMpls mpls = new ControllerCommands.TunnelMonitorMpls();
            if (PhysicalSwitchType.MPLS.toString().equals(physicalSwitchVO.getType())) {
                mpls.setM_ip(physicalSwitchVO.getmIP());
                mpls.setUsername(physicalSwitchVO.getUsername());
                mpls.setPassword(physicalSwitchVO.getPassword());
                mpls.setSwitch_type(physicalSwitchVO.getSwitchModel().getModel());
                mpls.setSub_type(physicalSwitchVO.getSwitchModel().getSubModel());
                mpls.setVlan_id(tunnelSwitchPortVO.getVlan() + 1);
                mpls.setPort_name(switchPortVO.getPortName());
                mpls.setBandwidth(tunnelVO.getBandwidth());
                mpls.setVni(tunnelVO.getVsi());
                mplsList.add(mpls);
            } else if (PhysicalSwitchType.SDN.toString().equals(physicalSwitchVO.getType())) {
                // 获取上联口对应的物理交换机作为mpls数据
                PhysicalSwitchUpLinkRefVO upLinkRef = (PhysicalSwitchUpLinkRefVO) Q.New(PhysicalSwitchUpLinkRefVO.class)
                        .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid, physicalSwitchVO.getUuid())
                        .list().get(0);
                PhysicalSwitchVO uplinkPhysicalSwitch = Q.New(PhysicalSwitchVO.class).eq(PhysicalSwitchVO_.uuid, upLinkRef.getUplinkPhysicalSwitchUuid()).find();

                mpls.setM_ip(uplinkPhysicalSwitch.getmIP());
                mpls.setUsername(uplinkPhysicalSwitch.getUsername());
                mpls.setPassword(uplinkPhysicalSwitch.getPassword());
                mpls.setSwitch_type(uplinkPhysicalSwitch.getSwitchModel().getModel());
                mpls.setSub_type(uplinkPhysicalSwitch.getSwitchModel().getSubModel());
                mpls.setVlan_id(tunnelSwitchPortVO.getVlan() + 1);
                mpls.setPort_name(switchPortVO.getPortName());
                mpls.setVni(tunnelVO.getVsi());

                mplsList.add(mpls);

                ControllerCommands.TunnelMonitorSdn sdn = new ControllerCommands.TunnelMonitorSdn();
                sdn.setM_ip(physicalSwitchVO.getmIP());
                sdn.setUplink(upLinkRef.getUplinkPhysicalSwitchPortName());
                sdn.setBandwidth(tunnelVO.getBandwidth());
                sdn.setVlan_id(tunnelSwitchPortVO.getVlan() + 1);

                if (tunnelSwitchPortVO.getSortTag().equals(InterfaceType.A.toString())) {
                    sdn.setNw_src(monitorIp.get(InterfaceType.A.toString()));
                    sdn.setNw_dst(monitorIp.get(InterfaceType.Z.toString()));
                    sdn.setIn_port(monitorPort.get(InterfaceType.A.toString()));
                } else if (tunnelSwitchPortVO.getSortTag().equals(InterfaceType.Z.toString())) {
                    sdn.setNw_src(monitorIp.get(InterfaceType.Z.toString()));
                    sdn.setNw_dst(monitorIp.get(InterfaceType.A.toString()));
                    sdn.setIn_port(monitorPort.get(InterfaceType.Z.toString()));
                }

                sdnList.add(sdn);
            }
        }

        return ControllerCommands.TunnelMonitorCommand.valueOf(sdnList, mplsList);
    }

    /***
     * 按tunnel交换机端口，查询监控主机uuid
     * @param switchPortUuid
     * @return 监控主机uuid
     */
    private String getHostUuid(String switchPortUuid) {
        String switcUuid = Q.New(SwitchPortVO.class).
                eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class).
                eq(SwitchVO_.uuid, switcUuid)
                .select(SwitchVO_.physicalSwitchUuid).findValue();

        List<String> hostUuids = Q.New(HostSwitchMonitorVO.class).
                eq(HostSwitchMonitorVO_.physicalSwitchUuid, physicalSwitchUuid).
                select(HostSwitchMonitorVO_.hostUuid).
                list();

        if (hostUuids.isEmpty())
            throw new IllegalArgumentException("获取监控主机失败");

        return hostUuids.get(0);
    }

    /**
     * 随机获取cidr中的IP
     *
     * @param startIp：cidr开始IP
     * @param endIp：cidr结束
     * @param locatedIps：已占用的IP
     * @return ip地址
     */
    private String getMonitorIp(String cidr, String startIp, String endIp, List<String> locatedIps) {
        String ip = "";

        long s = NetworkUtils.ipv4StringToLong(startIp);
        long e = NetworkUtils.ipv4StringToLong(endIp);
        for (; s <= e; s++) {
            ip = NetworkUtils.longToIpv4String(s);
            if (!locatedIps.contains(ip)) {
                locatedIps.add(ip);
            }
        }
        if (ip == null || ip.length() == 0) {
            throw new IllegalArgumentException("获取监控IP失败");
        }

        //掩码
        String mask = "/" + StringUtils.substringAfterLast(cidr, "/");

        return ip + mask;
    }

    /***
     *
     * @param monitorIp
     * @param monitorPort
     */
    private void getIpPort(List<TunnelMonitorVO> tunnelMonitorVOS, Map<String, String> monitorIp, Map<String, String> monitorPort) {
        for (TunnelMonitorVO tunnelMonitorVO : tunnelMonitorVOS) {
            TunnelSwitchPortVO tunnelSwitchPortVO = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.uuid, tunnelMonitorVO.getTunnelSwitchPortUuid())
                    .find();

            String physicalSwitchPortName = Q.New(HostSwitchMonitorVO.class)
                    .eq(HostSwitchMonitorVO_.hostUuid, tunnelMonitorVO.getHostUuid())
                    .select(HostSwitchMonitorVO_.physicalSwitchPortName)
                    .findValue();

            monitorIp.put(tunnelSwitchPortVO.getSortTag(), tunnelMonitorVO.getMonitorIp());
            monitorPort.put(tunnelSwitchPortVO.getSortTag(), physicalSwitchPortName);
        }
    }

    /**
     * 获取tunnel两端监控ip与监控端口
     *
     * @param monitorIp：监控IP集合
     * @param monitorPort：监控端口集合
     */
    private void getIpPort(String tunnelUuid, Map<String, String> monitorIp, Map<String, String> monitorPort) {
        String monitorHostSql = "select b.interfaceType,b.monitorIp,k.physicalSwitchPortName\n" +
                "  from TunnelMonitorVO a,TunnelMonitorInterfaceVO b,HostSwitchMonitorVO k\n" +
                " where b.tunnelMonitorUuid = a.uuid\n" +
                "   and k.hostUuid = b.hostUuid\n" +
                "   and a.tunnelUuid = :tunnelUuid";
        TypedQuery<Tuple> monitorHostQ = dbf.getEntityManager().createQuery(monitorHostSql, Tuple.class);
        monitorHostQ.setParameter("tunnelUuid", tunnelUuid);

        for (Tuple monitor : monitorHostQ.getResultList()) {
            monitorIp.put(monitor.get(0).toString(), monitor.get(1, String.class));
            monitorPort.put(monitor.get(0).toString(), monitor.get(2, String.class));
        }
    }

    /***
     * tunnel修改switch port，修改unnelMonitorVO.hostUuid
     */
    public void updateTunnelMonitorHostByTunnelSwitchPort(String tunnelSwitchPortUuid) {
        TunnelSwitchPortVO tunnelSwitchPortVO = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.uuid, tunnelSwitchPortUuid)
                .find();

        MonitorHostVO monitorHostVO = getHostBySwitchPort(tunnelSwitchPortVO.getSwitchPortUuid());
        TunnelMonitorVO tunnelMonitorVO = getTunnelMonitorByTunnelSwitchPort(tunnelSwitchPortUuid);

        if (tunnelMonitorVO != null && monitorHostVO != null) {
            tunnelMonitorVO.setHostUuid(monitorHostVO.getUuid());
            dbf.update(tunnelMonitorVO);
        }
    }

    /***
     * 按switchPortUuid查询SwitchVO
     * @param switchPortUuid
     * @return
     */
    public SwitchVO getSwitchBySwitchPort(String switchPortUuid) {
        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        return Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid, switchUuid)
                .find();
    }

    /***
     * 按switchPortUuid查询PhysicalSwitchVO
     * @param switchPortUuid
     * @return PhysicalSwitchVO
     */
    public PhysicalSwitchVO getPhysicalSwitchBySwitchPort(String switchPortUuid) {
        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid, switchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();

        return Q.New(PhysicalSwitchVO.class)
                .eq(PhysicalSwitchVO_.uuid, physicalSwitchUuid)
                .find();
    }

    /***
     * 按tunnelSwitchPortUuid查询PhysicalSwitchVO
     * @param tunnelSwitchPortUuid
     * @return PhysicalSwitchVO
     */
    public PhysicalSwitchVO getPhysicalSwitchByTunnelSwitchPort(String tunnelSwitchPortUuid) {
        String switchPortUuid = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.uuid, tunnelSwitchPortUuid)
                .select(TunnelSwitchPortVO_.switchPortUuid)
                .findValue();

        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid, switchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();

        return Q.New(PhysicalSwitchVO.class)
                .eq(PhysicalSwitchVO_.uuid, physicalSwitchUuid)
                .find();
    }

    /***
     * 按uuid获取SwitchModel
     * @param uuid
     * @return
     */
    public SwitchModelVO getSwitchModel(String uuid) {
        SwitchModelVO vo = Q.New(SwitchModelVO.class)
                .eq(SwitchModelVO_.uuid, uuid)
                .find();

        return vo;
    }

    /***
     * 按switchPortUuid查询监控Host
     * @param switchPortUuid
     * @return
     */
    public MonitorHostVO getHostBySwitchPort(String switchPortUuid) {
        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid, switchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();

        List<String> hostUuids = Q.New(HostSwitchMonitorVO.class)
                .eq(HostSwitchMonitorVO_.physicalSwitchUuid, physicalSwitchUuid)
                .select(HostSwitchMonitorVO_.hostUuid)
                .list();

        if (hostUuids.isEmpty())
            return null;

        List<MonitorHostVO> monitorHostVOS = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.uuid, hostUuids.get(0))
                .list();

        if (monitorHostVOS.isEmpty())
            return null;

        return monitorHostVOS.get(0);
    }

    public HostSwitchMonitorVO getHostSwitchMonitorByHostUuid(String hostUuid) {
        List<HostSwitchMonitorVO> vos = Q.New(HostSwitchMonitorVO.class)
                .eq(HostSwitchMonitorVO_.hostUuid, hostUuid)
                .list();

        if (vos.isEmpty())
            return null;

        return vos.get(0);
    }

    /***
     * 按tunnelUuid查询监控通道
     * @param tunnelUuid
     * @return
     */
    public List<TunnelMonitorVO> getTunnelMonitorByTunnel(String tunnelUuid) {
        return Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid)
                .list();
    }

    public TunnelSwitchPortVO getTunnelSwitchPortByUuid(String uuid) {
        return Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.uuid, uuid)
                .find();
    }

    /***
     * 按tunnelSwitchPortUuid查询监控通道
     * @return
     */
    public TunnelMonitorVO getTunnelMonitorByTunnelSwitchPort(String tunnelSwitchPortUuid) {
        return Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelSwitchPortUuid, tunnelSwitchPortUuid)
                .find();
    }

    /***
     * monitor获取tunnel对应的两端SwitchPort（只取A/Z）
     * @param tunnelId
     * @return List<TunnelSwitchPortVO
     */
    public List<TunnelSwitchPortVO> getMonitorTunnelSwitchPortByTunnelId(String tunnelId) {
        Set sortTags = new HashSet();
        sortTags.add("A");
        sortTags.add("Z");
        List<TunnelSwitchPortVO> portVOS = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, tunnelId)
                .in(TunnelSwitchPortVO_.sortTag, sortTags)
                .list();

        return portVOS;
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
        if (msg instanceof APICreateMonitorHostMsg) {
            validate((APICreateMonitorHostMsg) msg);
        } else if (msg instanceof APICreateHostSwitchMonitorMsg) {
            validate((APICreateHostSwitchMonitorMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateHostSwitchMonitorMsg msg) {
        //判断监控机和物理交换机所属节点是否一样
        MonitorHostVO hostVO = dbf.findByUuid(msg.getHostUuid(), MonitorHostVO.class);
        String hostNodeUuid = hostVO.getNodeUuid();
        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(msg.getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
        String physicalNodeUuid = physicalSwitchVO.getNodeUuid();
        if (!hostNodeUuid.equals(physicalNodeUuid)) {
            throw new ApiMessageInterceptionException(argerr("该监控机不能监控非该节点下的物理交换机 "));
        }

        //判断监控口在该物理交换机下是否开了业务
        String sql = "select count(a.uuid) from SwitchPortVO a, SwitchVO b " +
                "where a.switchUuid = b.uuid " +
                "and b.physicalSwitchUuid = :physicalSwitchUuid and a.portName = :portName ";
        TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
        vq.setParameter("physicalSwitchUuid", msg.getPhysicalSwitchUuid());
        vq.setParameter("portName", msg.getPhysicalSwitchPortName());
        Long count = vq.getSingleResult();
        if (count > 0) {
            throw new ApiMessageInterceptionException(argerr("该端口已经在业务口被录用，不能创建监控口 "));
        }

        //判断监控口名称在该物理交换机下是否存在
        SimpleQuery<HostSwitchMonitorVO> q = dbf.createQuery(HostSwitchMonitorVO.class);
        q.add(HostSwitchMonitorVO_.physicalSwitchUuid, SimpleQuery.Op.EQ, msg.getPhysicalSwitchUuid());
        q.add(HostSwitchMonitorVO_.physicalSwitchPortName, SimpleQuery.Op.EQ, msg.getPhysicalSwitchPortName());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr("physicalSwitchPortName %s is already exist ", msg.getPhysicalSwitchPortName()));

        //同一个监控机的网卡名称要唯一
        SimpleQuery<HostSwitchMonitorVO> q2 = dbf.createQuery(HostSwitchMonitorVO.class);
        q2.add(HostSwitchMonitorVO_.hostUuid, SimpleQuery.Op.EQ, msg.getHostUuid());
        q2.add(HostSwitchMonitorVO_.interfaceName, SimpleQuery.Op.EQ, msg.getInterfaceName());
        if (q2.isExists())
            throw new ApiMessageInterceptionException(argerr("interfaceName %s is already exist ", msg.getInterfaceName()));

    }

    private void validate(APICreateMonitorHostMsg msg) {
        //判断code是否已经存在
        SimpleQuery<MonitorHostVO> q = dbf.createQuery(MonitorHostVO.class);
        q.add(MonitorHostVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr("host's code %s is already exist ", msg.getCode()));

        //验证hostIp地址是否合法
        if (isPortEmpty(msg.getHostIp())) {
            msg.setHostIp(msg.getHostIp() + ":" + "22");
        }
        validateHostIp(msg.getHostIp());
    }

    /**
     * 验证监控主机IP合法性
     *
     * @param hostIp：监控主机IP
     */
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
