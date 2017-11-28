package com.syscxp.tunnel.monitor;

import com.alibaba.fastjson.JSON;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.cloudbus.ResourceDestinationMaker;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.Q;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.falconapi.FalconApiCommands;
import com.syscxp.header.falconapi.FalconApiRestConstant;
import com.syscxp.header.host.HostState;
import com.syscxp.header.host.HostStatus;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.host.*;
import com.syscxp.header.tunnel.monitor.*;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.identity.IdentityInterceptor;
import com.syscxp.tunnel.sdnController.ControllerCommands;
import com.syscxp.tunnel.sdnController.ControllerRestConstant;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    private ThreadFacade thdf;
    @Autowired
    private RESTFacade evtf;
    @Autowired
    private IdentityInterceptor identityInterceptor;

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
        if (msg instanceof APIStartTunnelMonitorMsg) {
            handle((APIStartTunnelMonitorMsg) msg);
        } else if (msg instanceof APIRestartTunnelMonitorMsg) {
            handle((APIRestartTunnelMonitorMsg) msg);
        } else if (msg instanceof APIStopTunnelMonitorMsg) {
            handle((APIStopTunnelMonitorMsg) msg);
        } else if (msg instanceof APIQuerySpeedTestTunnelNodeMsg) {
            handle((APIQuerySpeedTestTunnelNodeMsg) msg);
        } else if (msg instanceof APICreateSpeedRecordsMsg) {
            handle((APICreateSpeedRecordsMsg) msg);
        } else if (msg instanceof APIQuerySpeedResultMsg) {
            handle((APIQuerySpeedResultMsg) msg);
        } else if (msg instanceof APIUpdateSpeedRecordsMsg) {
            handle((APIUpdateSpeedRecordsMsg) msg);
        } else if (msg instanceof APIDeleteSpeedRecordsMsg) {
            handle((APIDeleteSpeedRecordsMsg) msg);
        } else if (msg instanceof APICreateNettoolRecordMsg) {
            handle((APICreateNettoolRecordMsg) msg);
        } else if (msg instanceof APIQueryNettoolResultMsg) {
            handle((APIQueryNettoolResultMsg) msg);
        } else if (msg instanceof APIQueryNettoolNodeMsg) {
            handle((APIQueryNettoolNodeMsg) msg);
        } else if (msg instanceof APIQueryMonitorResultMsg) {
            handle((APIQueryMonitorResultMsg) msg);
        } else if (msg instanceof APICreateSpeedTestTunnelMsg) {
            handle((APICreateSpeedTestTunnelMsg) msg);
        } else if (msg instanceof APIDeleteSpeedTestTunnelMsg) {
            handle((APIDeleteSpeedTestTunnelMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    @Transactional
    private void handle(APIStartTunnelMonitorMsg msg) {
        APIStartTunnelMonitorEvent event = new APIStartTunnelMonitorEvent(msg.getId());

        try {
            // 创建监控通道
            List<TunnelMonitorVO> tunnelMonitorVOS = createTunnelMonitor(msg.getTunnelUuid(), msg.getMonitorCidr());

            // 控制器命令下发
            // startControllerCommand(msg.getTunnelUuid(), tunnelMonitorVOS);

            // 同步icmp
            icmpSync(msg.getSession().getAccountUuid(), msg.getTunnelUuid(), msg.getMonitorCidr(), tunnelMonitorVOS);

            // 开启agent监控
            startAgentMonitor(msg.getSession().getAccountUuid(), msg.getTunnelUuid(), msg.getMonitorCidr(), tunnelMonitorVOS);

            // 更新tunnel状态
            updateTunnel(msg.getTunnelUuid(), msg.getMonitorCidr(), TunnelMonitorState.Enabled, TunnelStatus.Connected);

            event.setInventories(TunnelMonitorInventory.valueOf(tunnelMonitorVOS));
        } catch (Exception e) {
            event.setError(Platform.operr("Failure to start tunnel monitor! %s", e.getMessage()));
        }

        bus.publish(event);
    }

    @Transactional
    private void handle(APIStopTunnelMonitorMsg msg) {
        APIStopTunnelMonitorEvent event = new APIStopTunnelMonitorEvent(msg.getId());

        try {
            stopTunnelMonitor(msg.getTunnelUuid());
        } catch (Exception e) {
            event.setError(Platform.operr("failed to stop monitor! %s", e.getMessage()));
        }
        bus.publish(event);
    }

    private void handle(APIRestartTunnelMonitorMsg msg) {
        APIRestartTunnelMonitorEvent event = new APIRestartTunnelMonitorEvent(msg.getId());
        try {
            // 获取监控通道数据
            List<TunnelMonitorVO> tunnelMonitorVOS = getTunnelMonitorByTunnel(msg.getTunnelUuid());

            // 控制器命令删除
            ControllerCommands.TunnelMonitorCommand cmd = getTunnelMonitorCommand(msg.getTunnelUuid(),  tunnelMonitorVOS);
            stopControllerCommand(cmd);

            // 更新监控IP
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
                    event.setError(Platform.operr("failed to generate monitor ip！" + e.getMessage()));
                }
            }

            // 控制器命令下发
            if (event.isSuccess()) {
                startControllerCommand(msg.getTunnelUuid(), tunnelMonitorVOS);

                // 同步icmp
                icmpSync(msg.getSession().getAccountUuid(), msg.getTunnelUuid(), msg.getMonitorCidr(), tunnelMonitorVOS);

                // 更新tunnel状态
                updateTunnel(msg.getTunnelUuid(), msg.getMonitorCidr(), TunnelMonitorState.Enabled, TunnelStatus.Connected);
            }
        } catch (Exception e) {
            event.setError(Platform.operr("failed to stop monitor! %s", e.getMessage()));
        }
        bus.publish(event);
    }

    /**
     * 创建监控通道 （创建完tunnel后手动开启）
     *
     * @param msg
     * @return：创建的监控通道
     */
    private List<TunnelMonitorVO> createTunnelMonitor(String tunnelUuid, String monitorCidr) throws UnknownHostException {
        List<TunnelMonitorVO> tunnelMonitorVOS = new ArrayList<TunnelMonitorVO>();

        // 按tunnel查找TunnelMonitorVO已经存在的IP
        List<String> locatedIps = getLocatedIps(tunnelUuid, monitorCidr);

        CIDRUtils cidrUtils = new CIDRUtils(monitorCidr);
        String startIp = cidrUtils.getNetworkAddress();
        String endIp = cidrUtils.getBroadcastAddress();

        // 获取tunnel两端交换机端口
        List<TunnelSwitchPortVO> portVOS = getMonitorTunnelSwitchPortByTunnelId(tunnelUuid);

        TunnelMonitorVO monitorVO = new TunnelMonitorVO();
        for (TunnelSwitchPortVO tunnelSwitchPortVO : portVOS) {
            // 按tunnel port查询是否有已存在的监控机与监控IP
            monitorVO = Q.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelSwitchPortUuid, tunnelSwitchPortVO.getUuid()).find();
            if (monitorVO != null) {
                tunnelMonitorVOS.add(monitorVO);
                locatedIps.add(monitorVO.getMonitorIp().substring(0, monitorVO.getMonitorIp().indexOf("/")));
                continue;
            } else {
                monitorVO = new TunnelMonitorVO();
                // 获取监控主机
                String hostUuid = getHostUuid(tunnelSwitchPortVO.getSwitchPortUuid());
                // 根据cidr获取监控IP
                String monitorIp = getMonitorIp(monitorCidr, startIp, endIp, locatedIps);

                monitorVO.setUuid(Platform.getUuid());
                monitorVO.setTunnelUuid(tunnelUuid);
                monitorVO.setTunnelSwitchPortUuid(tunnelSwitchPortVO.getUuid());
                monitorVO.setHostUuid(hostUuid);
                monitorVO.setMonitorIp(monitorIp);
                dbf.getEntityManager().persist(monitorVO);

                tunnelMonitorVOS.add(monitorVO);
            }
        }

        // dbf.getEntityManager().flush();

        if (tunnelMonitorVOS.isEmpty())
            throw new IllegalArgumentException(String.format(" Fail to create tunnel monitor！ %s ", tunnelUuid));

        return tunnelMonitorVOS;
    }

    /***
     * 获取已分配的监控ip
     * @param tunnelUuid
     * @return
     */
    private List<String> getLocatedIps(String tunnelUuid, String monitorCidr) {
        List<String> locateIps = new ArrayList<>();
//        TunnelVO tunnelVO = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).find();
//        for (TunnelSwitchPortVO tunnelSwitchPortVO : tunnelVO.getTunnelSwitchPortVOS()) {
//            TunnelMonitorVO tunnelMonitorVO = getExistTunnelMonitorByTunnelSwitchPost(tunnelSwitchPortVO.getSwitchPortUuid());
//            locateIps.add(tunnelMonitorVO.getMonitorIp().substring(0, tunnelMonitorVO.getMonitorIp().indexOf("/")));
//        }

        String prefix = monitorCidr.substring(0, monitorCidr.lastIndexOf("."));
        locateIps.add(prefix + ".0");
        locateIps.add(prefix + ".255");

        return locateIps;
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
                break;
            }
        }

        if (ip == null || ip.length() == 0)
            throw new IllegalArgumentException(String.format("Fail to generate monitor ip from CIDR %s", cidr));

        //掩码
        String mask = "/" + StringUtils.substringAfterLast(cidr, "/");

        return ip + mask;
    }

    /***
     * 监控关闭、tunnel中止
     * @param tunnelUuid
     */
    public void stopTunnelMonitor(String tunnelUuid) {
        // 获取监控通道数据
        List<TunnelMonitorVO> tunnelMonitorVOS = getTunnelMonitorByTunnel(tunnelUuid);

        // 控制器命令删除
        ControllerCommands.TunnelMonitorCommand cmd = getTunnelMonitorCommand(tunnelUuid,  tunnelMonitorVOS);
        stopControllerCommand(cmd);

        // 删除icmp
        icmpDelete(tunnelUuid);

        // 关闭agent监控
        stopAgentMonitor(tunnelUuid, tunnelMonitorVOS);

        // 更新tunnel状态
        updateTunnel(tunnelUuid, "", TunnelMonitorState.Disabled, TunnelStatus.Connected);
    }

    /***
     * 重启监控通道：tunnel修改vlan、调整带宽、修改端口调用
     * @param tunnelUuid
     */
    public void restartTunnelMonitor(String tunnelUuid, String accountUuid, String monitorCidr, ControllerCommands.TunnelMonitorCommand monitorCommand) {
        // 控制器命令删除
        stopControllerCommand(monitorCommand);

        // 获取监控通道数据
        List<TunnelMonitorVO> tunnelMonitorVOS = getTunnelMonitorByTunnel(tunnelUuid);
        // 控制器命令下发
        startControllerCommand(tunnelUuid, tunnelMonitorVOS);

        // 同步icmp
        icmpSync(accountUuid, tunnelUuid, monitorCidr, tunnelMonitorVOS);

        // 更新tunnel状态
        updateTunnel(tunnelUuid, "", TunnelMonitorState.Disabled, TunnelStatus.Connected);
    }

    /***
     * 删除监控通道：删除tunnel
     * @param tunnelUuid
     * @param event
     */
    @Transactional
    public void deleteTunnelMonitor(String tunnelUuid, APIEvent event) {
        // 获取监控通道数据
        List<TunnelMonitorVO> tunnelMonitorVOS = getTunnelMonitorByTunnel(tunnelUuid);

        // 控制器命令删除
        ControllerCommands.TunnelMonitorCommand cmd = getTunnelMonitorCommand(tunnelUuid,  tunnelMonitorVOS);
        stopControllerCommand(cmd);

        // 删除icmp
        icmpDelete(tunnelUuid);

        // 停止agent监控
        stopAgentMonitor(tunnelUuid, tunnelMonitorVOS);

        // 删除监控通道数据
        updateTunnel(tunnelUuid, "", TunnelMonitorState.Disabled, TunnelStatus.Connected);
    }

    /***
     * 获取RYU控制器服务url
     * @param method
     * @return
     */
    private String getControllerUrl(String method) {
        String url = CoreGlobalProperty.CONTROLLER_MANAGER_URL + method;

        return url;
    }

    private void sendControllerCommand(String url, ControllerCommands.TunnelMonitorCommand command) {
        ControllerCommands.ControllerRestResponse response = new ControllerCommands.ControllerRestResponse();
        try {
            String jsonCommand = JSONObjectUtil.toJsonString(command);
            response = evtf.syncJsonPost(url, jsonCommand, ControllerCommands.ControllerRestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        if (!response.isSuccess())
            throw new RuntimeException(String.format("Failure to execute RYU start command! Error:%s", response.getMsg()));
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
     * 监控命令下发至控制器
     * @param tunnelUuid
     * @param tunnelMonitorVOS
     * @return
     */
    private void startControllerCommand(String tunnelUuid, List<TunnelMonitorVO> tunnelMonitorVOS) {
        String url = getControllerUrl(ControllerRestConstant.START_TUNNEL_MONITOR);
        ControllerCommands.TunnelMonitorCommand cmd = getTunnelMonitorCommand(tunnelUuid, tunnelMonitorVOS);

        sendControllerCommand(url, cmd);
    }

    /**
     * 控制器命令删除：关闭监控、中止tunnel
     *
     */
    private void stopControllerCommand(ControllerCommands.TunnelMonitorCommand cmd) {
        String url = CoreGlobalProperty.CONTROLLER_MANAGER_URL;
        sendControllerCommand(url, cmd);
    }

    /**
     * 开启监控、tunnel修改且监控为开启状态时同步ICMP到falcon_portal数据库
     * 下发监控agent配置
     */
    private void icmpSync(String accountUuid, String tunnelUuid, String monitorCidr, List<TunnelMonitorVO> tunnelMonitorVOS) {
        String falconUrl = getFalconServerUrl(FalconApiRestConstant.ICMP_SYNC);
        FalconApiCommands.RestResponse falconRsp = new FalconApiCommands.RestResponse();
        try {
            FalconApiCommands.Icmp icmp = getIcmp(accountUuid, tunnelUuid, monitorCidr, tunnelMonitorVOS);
            String icmpJson = JSONObjectUtil.toJsonString(icmp);

            falconRsp = evtf.syncJsonPost(falconUrl, icmpJson, FalconApiCommands.RestResponse.class);

        } catch (Exception e) {
            falconRsp.setSuccess(false);
            falconRsp.setMsg(String.format("unable to post %s. %s", falconUrl, e.getMessage()));
        }

        if (!falconRsp.isSuccess())
            throw new RuntimeException(String.format("failure to sync icmp: tunnelUuid: %s , Error: %s", tunnelUuid, falconRsp.getMsg()));
    }

    /***
     * 获取ICMP信息
     * @param accountUuid
     * @param tunnelUuid
     * @param monitorCidr
     * @param tunnelMonitorVOS
     * @return
     */
    public FalconApiCommands.Icmp getIcmp(String accountUuid, String tunnelUuid, String monitorCidr, List<TunnelMonitorVO> tunnelMonitorVOS) {
        FalconApiCommands.Icmp icmp = new FalconApiCommands.Icmp();
        TunnelVO tunnelVO = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).find();

        icmp.setTunnel_id(tunnelUuid);
        icmp.setTunnel_name(tunnelVO.getName());
        icmp.setUser_id(accountUuid);
        icmp.setCidr(monitorCidr);
        icmp.setBandwidth(tunnelVO.getBandwidth());

        for (TunnelMonitorVO tunnelMonitorVO : tunnelMonitorVOS) {
            TunnelSwitchPortVO tunnelSwitchPortVO = getTunnelSwitchPortByUuid(tunnelMonitorVO.getTunnelSwitchPortUuid());
            SwitchVO switchVO = getSwitchBySwitchPort(tunnelSwitchPortVO.getSwitchPortUuid());
            HostSwitchMonitorVO hostSwitchMonitorVO = getHostSwitchMonitorByHostUuid(tunnelMonitorVO.getHostUuid());
            MonitorHostVO monitorHostVO = Q.New(MonitorHostVO.class).eq(MonitorHostVO_.uuid, hostSwitchMonitorVO.getHostUuid()).find();
            if (monitorHostVO == null)
                throw new IllegalArgumentException(String.format("failed to get host %s", hostSwitchMonitorVO.getHostUuid()));

            if (InterfaceType.A.toString().equals(tunnelSwitchPortVO.getSortTag())) {
                icmp.setHostA_ip(monitorHostVO.getHostIp());
                icmp.setEndpointA_mip(tunnelMonitorVO.getMonitorIp().substring(0, tunnelMonitorVO.getMonitorIp().indexOf("/")));
                icmp.setEndpointA_vlan(tunnelSwitchPortVO.getVlan());
                icmp.setEndpointA_id(switchVO.getEndpointUuid());
                icmp.setEndpointA_ip(switchVO.getPhysicalSwitch().getmIP());
                icmp.setEndpointA_interface(hostSwitchMonitorVO.getInterfaceName());
            } else if (InterfaceType.Z.toString().equals(tunnelSwitchPortVO.getSortTag())) {
                icmp.setHostB_ip(monitorHostVO.getHostIp());
                icmp.setEndpointB_mip(tunnelMonitorVO.getMonitorIp().substring(0, tunnelMonitorVO.getMonitorIp().indexOf("/")));
                icmp.setEndpointB_vlan(tunnelSwitchPortVO.getVlan());
                icmp.setEndpointB_id(switchVO.getEndpointUuid());
                icmp.setEndpointB_ip(switchVO.getPhysicalSwitch().getmIP());
                icmp.setEndpointB_interface(hostSwitchMonitorVO.getInterfaceName());
            }
        }

        return icmp;
    }

    /***
     * 删除falcon_portal ICMP数据：关闭监控、tunnel删除、tunnel中止
     * @param tunnelUuid
     */
    private void icmpDelete(String tunnelUuid) {
        String url = getFalconServerUrl(FalconApiRestConstant.ICMP_DELETE);
        FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();

        try {
            FalconApiCommands falconApiCommands = new FalconApiCommands();
            falconApiCommands.setTunnel_id(tunnelUuid);
            String deleteJson = JSONObjectUtil.toJsonString(falconApiCommands);

            response = evtf.syncJsonPost(url, deleteJson, FalconApiCommands.RestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        if (!response.isSuccess())
            throw new RuntimeException(String.format("failed to delete icmp data! Error: %s", response.getMsg()));
    }

    /**
     * 获取falcon API地址
     *
     * @param method：方法名称
     * @return falcon api url地址
     */
    public String getFalconServerUrl(String method) {
        return String.format("http://%s:%s%s",
                CoreGlobalProperty.FALCON_API_IP, CoreGlobalProperty.FALCON_API_PORT, method);
    }

    /***
     * 获取监控agent 服务url
     * @param hostIp
     * @param method
     * @return
     */
    private String getMonitorAgentUrl(String hostIp, String method) {
        String url = "http://" + hostIp + ":" + MonitorAgentConstant.SERVER_PORT + method;

        return url;
    }

    /***
     * 发送监控agent命令
     * @param url
     * @param command
     * @return
     */
    private void sendMonitorAgentCommand(String url, String command) {
        MonitorAgentCommands.RestResponse response = new MonitorAgentCommands.RestResponse();
        try {
            response = evtf.syncJsonPost(url,
                    command, MonitorAgentCommands.RestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        if (!response.isSuccess())
            throw new RuntimeException(String.format("failed to send agent command! command! Error: %s", response.getMsg()));
    }

    /***
     * 获取OpenTSDB服务url
     * @param method
     * @return
     */
    private String getOpenTSDBUrl(String method) {
        String url = CoreGlobalProperty.OPENTSDB_SERVER_URL + method;

        return url;
    }

    /***
     * 开启agnet监控
     * @return
     */
    private void startAgentMonitor(String accountUuid, String tunnelUuid, String monitorCidr, List<TunnelMonitorVO> tunnelMonitorVOS) {
        FalconApiCommands.Icmp icmp = getIcmp(accountUuid, tunnelUuid, monitorCidr, tunnelMonitorVOS);
        // 下发监控agent配置
        String icmpJson = formatIcmpCommand(icmp);
        MonitorAgentCommands.RestResponse agentResp = new MonitorAgentCommands.RestResponse();
        agentResp.setSuccess(true);
        for (TunnelMonitorVO tunnelMonitor : tunnelMonitorVOS) {
            if (agentResp.isSuccess()) {
                String hostIp = getMonitorHostIpByTunnelMonitorUuid(tunnelMonitor.getUuid());
                String url = getMonitorAgentUrl(hostIp, MonitorAgentConstant.START_MONITOR);

                sendMonitorAgentCommand(url, icmpJson);
            }
        }

        if (!agentResp.isSuccess())
            throw new RuntimeException(String.format("Failure to start agent monitor! Error: %s", agentResp.getMsg()));
    }

    /***
     * 关闭agent监控
     * @param tunnelUuid
     * @param tunnelMonitorVOS
     */
    private void stopAgentMonitor(String tunnelUuid, List<TunnelMonitorVO> tunnelMonitorVOS) {
        for (TunnelMonitorVO tunnelMonitor : tunnelMonitorVOS) {
            String hostIp = getMonitorHostIpByTunnelMonitorUuid(tunnelMonitor.getUuid());
            Map<String, String> tunnelMap = new HashMap<>();
            tunnelMap.put("tunnel_id", tunnelUuid);

            MonitorAgentCommands.RestResponse response = new MonitorAgentCommands.RestResponse();
            String url = getMonitorAgentUrl(hostIp, MonitorAgentConstant.STOP_MONITOR);
            try {
                response = evtf.syncJsonPost(url,
                        JSONObjectUtil.toJsonString(tunnelMap), MonitorAgentCommands.RestResponse.class);
            } catch (Exception e) {
                response.setSuccess(false);
                response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
            }

            if (!response.isSuccess())
                throw new RuntimeException(String.format("Failuere to stop monitor on monitor host %s！ Error: %s", hostIp, response.getMsg()));
        }
    }

    /***
     * 修改agent监控
     */
    private void updateAgentMonitor() {
        // TODO: 修改监控接口


    }

    private String formatIcmpCommand(FalconApiCommands.Icmp icmp){
        List<FalconApiCommands.Icmp> icmps = new ArrayList<>();
        icmps.add(icmp);

        Map<String,Object> strategies = new HashMap<>();
        strategies.put("strategies",icmps);

        Map<String,Object> strategyList = new HashMap<>();
        strategyList.put("strategyList",strategies);

        String icmpsJson = JSONObjectUtil.toJsonString(strategyList);

        return icmpsJson;
    }

    /**
     * 创建监控通道测速
     *
     * @param msg
     * @return 测速记录
     */
    private SpeedRecordsVO generateSpeedRecord(APICreateSpeedRecordsMsg msg) {
        SpeedRecordsVO vo = new SpeedRecordsVO();

        vo.setUuid(Platform.getUuid());
        vo.setTunnelUuid(msg.getTunnelUuid());
        vo.setProtocolType(msg.getProtocolType());
        vo.setDuration(msg.getDuration());
        vo.setStatus(SpeedRecordStatus.TESTING);

        if (msg.getSession() != null) {
            if (StringUtils.isNotEmpty(msg.getSession().getUuid())) {
                SessionInventory sessionInventory = identityInterceptor.getSessionInventory(msg.getSession().getUuid());
                vo.setAccountUuid(sessionInventory.getAccountUuid());
            }
        }

        TunnelMonitorVO srcTunnelMonitor = getTunnelMonitorByNodeAndTunnel(msg.getSrcNodeUuid(), msg.getTunnelUuid());
        vo.setSrcTunnelMonitorUuid(srcTunnelMonitor.getUuid());

        TunnelMonitorVO dstTunnelMonitors = getTunnelMonitorByNodeAndTunnel(msg.getDstNodeUuid(), msg.getTunnelUuid());
        vo.setDstTunnelMonitorUuid(dstTunnelMonitors.getUuid());

        return vo;
    }

    /***
     * 发送测速命令
     * @param vo
     */
    private void sendSpeedTestCommand(SpeedRecordsVO vo) {
        Map<String, Object> commamdMap = getSpeedTestCommand(vo);

        // server
        String serverCommand = JSONObjectUtil.toJsonString(commamdMap.get(MonitorAgentCommands.SpeedCommandType.server.toString()));
        String serverUrl = getMonitorAgentUrl(getMonitorHostIpByTunnelMonitorUuid(vo.getSrcTunnelMonitorUuid()), MonitorAgentConstant.IPERF);
        sendMonitorAgentCommand(serverUrl, serverCommand);

        String clientCommand = JSONObjectUtil.toJsonString(commamdMap.get(MonitorAgentCommands.SpeedCommandType.client.toString()));
        String clientUrl = getMonitorAgentUrl(getMonitorHostIpByTunnelMonitorUuid(vo.getDstTunnelMonitorUuid()), MonitorAgentConstant.IPERF);
        sendMonitorAgentCommand(clientUrl, clientCommand);
    }

    private String getMonitorHostIpByTunnelMonitorUuid(String tunnelMonitorUuid) {
        TunnelMonitorVO tunnelMonitorVO = Q.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.uuid, tunnelMonitorUuid).find();
        String monitorHostIp = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.uuid, tunnelMonitorVO.getHostUuid())
                .select(MonitorHostVO_.hostIp).findValue();

        return monitorHostIp;
    }

    /**
     * 获取测速下发命令
     *
     * @param vo：SpeedRecordsVO
     * @return：server、client测速命令
     */
    private Map<String, Object> getSpeedTestCommand(SpeedRecordsVO vo) {
        Map<String, Object> commandMap = new HashMap<>();

        TunnelVO tunnelVO = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, vo.getTunnelUuid()).find();
        int port = MonitorAgentCommands.getPort();

        // 服务端
        MonitorAgentCommands.SpeedRecordServer server = new MonitorAgentCommands.SpeedRecordServer();
        server.setGuid(vo.getUuid());
        server.setTunnel_id(vo.getTunnelUuid());
        server.setTime(vo.getDuration());
        server.setType(MonitorAgentCommands.SpeedCommandType.server);
        server.setPort(port);

        TunnelMonitorVO srcTunnelMonitor = Q.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.uuid, vo.getSrcTunnelMonitorUuid()).find();
        server.setSrc_ip(StringUtils.substringBefore(srcTunnelMonitor.getMonitorIp(), "/"));
        server.setInterface_name(getHostSwitchMonitorByHostUuid(srcTunnelMonitor.getHostUuid()).getInterfaceName());
        server.setVlan(getTunnelSwitchPortByUuid(srcTunnelMonitor.getTunnelSwitchPortUuid()).getVlan());
        commandMap.put(MonitorAgentCommands.SpeedCommandType.server.toString(), server);

        // 客户端
        MonitorAgentCommands.SpeedRecordClient client = new MonitorAgentCommands.SpeedRecordClient();
        client.setTunnel_id(vo.getTunnelUuid());
        client.setTime(vo.getDuration());
        client.setType(MonitorAgentCommands.SpeedCommandType.client);
        client.setPort(port);

        TunnelMonitorVO dstTunnelMonitor = Q.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.uuid, vo.getDstTunnelMonitorUuid()).find();
        client.setSrc_ip(StringUtils.substringBefore(dstTunnelMonitor.getMonitorIp(), "/"));
        client.setInterface_name(getHostSwitchMonitorByHostUuid(dstTunnelMonitor.getHostUuid()).getInterfaceName());
        client.setVlan(getTunnelSwitchPortByUuid(dstTunnelMonitor.getTunnelSwitchPortUuid()).getVlan());
        client.setDst_ip(StringUtils.substringBefore(srcTunnelMonitor.getMonitorIp(), "/"));
        client.setProtocol(vo.getProtocolType());

        Long bandwidth = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, vo.getTunnelUuid()).select(TunnelVO_.bandwidth).findValue();
        client.setBandwidth(bandwidth);

        commandMap.put(MonitorAgentCommands.SpeedCommandType.client.toString(), client);

        return commandMap;
    }

    /***
     * 获取OpenTSDB查询条件
     * @param msg
     * @return
     */
    private String getOpenTSDBQueryCondition(APIQueryMonitorResultMsg msg) {
        List<OpenTSDBCommands.Query> queries = new ArrayList<>();

        TunnelVO tunnel = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, msg.getTunnelUuid()).find();
        for (TunnelSwitchPortVO tunnelPort : tunnel.getTunnelSwitchPortVOS()) {
            if (tunnelPort.getSortTag().equals(InterfaceType.A.toString()) ||
                    tunnelPort.getSortTag().equals(InterfaceType.Z.toString())) {
                PhysicalSwitchVO physicalSwitch = getPhysicalSwitchBySwitchPort(tunnelPort.getSwitchPortUuid());
                if (physicalSwitch == null)
                    throw new IllegalArgumentException(String.format("No physical switch exist under switch port %s", tunnelPort.getSwitchPortUuid()));

                for (String metric : msg.getMetrics()) {
                    OpenTSDBCommands.Tags tags = new OpenTSDBCommands.Tags(physicalSwitch.getmIP(), "Vlanif" + tunnelPort.getVlan());
                    OpenTSDBCommands.Query query = new OpenTSDBCommands.Query("avg", metric, tags);
                    queries.add(query);
                }
            }
        }
        OpenTSDBCommands.QueryCondition condition = new OpenTSDBCommands.QueryCondition();
        condition.setStart(msg.getStart());
        condition.setEnd(msg.getEnd());
        condition.setQueries(queries);

        return JSONObjectUtil.toJsonString(condition);
    }

    /***
     * 更新tunnel监控状态
     * @param tunnelUuid
     * @param monitorCidr
     * @param monitorState
     */
    private void updateTunnel(String tunnelUuid, String monitorCidr, TunnelMonitorState monitorState, TunnelStatus tunnelStatus) {
        // 更新tunnel状态
        TunnelVO tunnelVO = Q.New(TunnelVO.class)
                .eq(TunnelVO_.uuid, tunnelUuid)
                .find();

        tunnelVO.setMonitorState(monitorState);
        if (StringUtils.isNotEmpty(monitorCidr)) {
            tunnelVO.setMonitorCidr(monitorCidr);
        }
        if (StringUtils.isNotEmpty(tunnelStatus.toString())) {
            tunnelVO.setStatus(tunnelStatus);
        }

        dbf.getEntityManager().persist(tunnelVO);
    }

    private void handle(APIQuerySpeedTestTunnelNodeMsg msg){
        APIQuerySpeedTestTunnelNodeReply reply = new APIQuerySpeedTestTunnelNodeReply();
        try{
            List<SpeedTestTunnelVO> speedTestTunnelVOS = Q.New(SpeedTestTunnelVO.class).list();
            for(SpeedTestTunnelVO speedTestTunnelVO : speedTestTunnelVOS){
                if(speedTestTunnelVO.getTunnelVO().getState().equals(TunnelState.Enabled ) &&
                        speedTestTunnelVO.getTunnelVO().getStatus().equals(TunnelStatus.Connected)&&
                        speedTestTunnelVO.getTunnelVO().getMonitorState().equals(TunnelMonitorState.Enabled)) {

                    speedTestTunnelVOS.remove(speedTestTunnelVO);
                }
            }

            reply.setInventories(SpeedTestTunnelNodeInventory.valueOf(speedTestTunnelVOS));
        }catch (Exception e){
            reply.setError(Platform.argerr("failed to query speed test node!"));
        }

        bus.reply(msg,reply);
    }

    private void handle(APICreateSpeedRecordsMsg msg) {
        APICreateSpeedRecordsEvent event = new APICreateSpeedRecordsEvent(msg.getId());

        try {
            // 创建纪录
            SpeedRecordsVO vo = generateSpeedRecord(msg);

            // 下发测速命令
            sendSpeedTestCommand(vo);

            // 保存测试记录
            vo = dbf.persistAndRefresh(vo);

            event.setInventory(StartSpeedRecordsInventory.valueOf(vo, getMonitorHostIpByTunnelMonitorUuid(vo.getSrcTunnelMonitorUuid())));
        } catch (Exception e) {
            event.setError(Platform.operr("Speed test failure！ %s", e.getMessage()));
        }

        bus.publish(event);
    }

    private void handle(APIQuerySpeedResultMsg msg) {
        APIQuerySpeedResultReply reply = new APIQuerySpeedResultReply();

        List<MonitorAgentCommands.SpeedResult> results = new ArrayList<>();
        try {
            Map<String, String> map = new HashMap<>();
            map.put("guid", msg.getUuid());
            String command = JSONObjectUtil.toJsonString(map);
            String url = getMonitorAgentUrl(msg.getHostIp(), MonitorAgentConstant.IPERF_RESULT);

            String resp = evtf.getRESTTemplate().postForObject(url, command, String.class);

            results = JSON.parseArray(resp, MonitorAgentCommands.SpeedResult.class);
        } catch (Exception e) {
            reply.setError(Platform.argerr("Failure to execute speed test command! %s", e.getMessage()));
        }

        if (reply.isSuccess())
            reply.setInventories(SpeedResultInventory.valueOf(results));

        bus.reply(msg, reply);
    }

    private void handle(APIUpdateSpeedRecordsMsg msg) {
        SpeedRecordsVO vo = dbf.findByUuid(msg.getUuid(), SpeedRecordsVO.class);

        vo.setAvgSpeed(msg.getAvgSpeed());
        vo.setMaxSpeed(msg.getMaxSpeed());
        vo.setMinSpeed(msg.getMinSpeed());
        vo.setStatus(msg.getStatus());

        vo = dbf.updateAndRefresh(vo);

        APIUpdateSpeedRecordsEvent event = new APIUpdateSpeedRecordsEvent(msg.getId());
        event.setInventory(SpeedRecordsInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIDeleteSpeedRecordsMsg msg) {
        SpeedRecordsVO vo = dbf.findByUuid(msg.getUuid(), SpeedRecordsVO.class);

        dbf.remove(vo);

        APIDeleteSpeedRecordsEvent event = new APIDeleteSpeedRecordsEvent(msg.getId());
        event.setInventory(SpeedRecordsInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APICreateNettoolRecordMsg msg) {
        APICreateNettoolRecordEvent event = new APICreateNettoolRecordEvent(msg.getId());

        try {
            MonitorAgentCommands.NettoolCommand nettoolCommand = new MonitorAgentCommands.NettoolCommand();
            nettoolCommand.setCommand(msg.getCommand());
            nettoolCommand.setGuid(Platform.getUuid());
            nettoolCommand.setRemote_ip(msg.getRemoteIp());
            String command = JSONObjectUtil.toJsonString(nettoolCommand);

            MonitorHostVO host = getMonitorHostByNodeAndType(msg.getNodeUuid(), MonitorType.NETTOOL);

            if (host == null)
                event.setError(Platform.operr("No monitor host exist under node %s", msg.getNodeUuid()));

            if (event.isSuccess()) {
                String url = getMonitorAgentUrl(host.getHostIp(), MonitorAgentConstant.NETTOOL);
                sendMonitorAgentCommand(url, command);
            }

            if (event.isSuccess())
                event.setInventory(NettoolRecordInventory.valueOf(nettoolCommand, host.getHostIp()));
        } catch (Exception e) {
            event.setError(Platform.operr("failed to create speed test record! %s", msg.getNodeUuid()));
        }

        bus.publish(event);
    }

    private void handle(APIQueryNettoolResultMsg msg) {
        APIQueryNettoolResultReply reply = new APIQueryNettoolResultReply();
        List<MonitorAgentCommands.NettoolResult> result = new ArrayList<>();
        try {
            Map<String, String> map = new HashMap<>();
            map.put("guid", msg.getGuid());
            String command = JSONObjectUtil.toJsonString(map);
            String url = getMonitorAgentUrl(msg.getHostIp(), MonitorAgentConstant.NETTOOL_RESULT);

            String resp = evtf.getRESTTemplate().postForObject(url, command, String.class);

            result = JSON.parseArray(resp, MonitorAgentCommands.NettoolResult.class);
        } catch (Exception e) {
            reply.setError(Platform.argerr("Failure to execute speed test command! %s", e.getMessage()));
        }

        if (reply.isSuccess())
            reply.setInventories(NettoolResultInventory.valueOf(result));

        bus.reply(msg, reply);
    }

    private void handle(APIQueryNettoolNodeMsg msg) {
        APIQueryNettoolNodeReply reply = new APIQueryNettoolNodeReply();

        List<MonitorHostVO> monitorHostVOS = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.monitorType, MonitorType.NETTOOL)
                .eq(MonitorHostVO_.status, HostStatus.Connected)
                .eq(MonitorHostVO_.state, HostState.Enabled)
                .list();

        reply.setInventories(MonitorHostInventory.valueOf1(monitorHostVOS));

        bus.reply(msg, reply);
    }

    private void handle(APIQueryMonitorResultMsg msg) {
        APIQueryMonitorResultReply reply = new APIQueryMonitorResultReply();

        try {
            String url = getOpenTSDBUrl(OpenTSDBCommands.restMethod.OPEN_TSDB_QUERY);
            String condition = getOpenTSDBQueryCondition(msg);

            String resp = evtf.getRESTTemplate().postForObject(url, condition, String.class);
            List<OpenTSDBCommands.QueryResult> results = JSON.parseArray(resp, OpenTSDBCommands.QueryResult.class);
            for (OpenTSDBCommands.QueryResult result : results) {
                String mIP = result.getTags().getEndpoint();
                List<PhysicalSwitchVO> physicalSwitchVOS = Q.New(PhysicalSwitchVO.class)
                        .eq(PhysicalSwitchVO_.mIP, mIP)
                        .list();

                if (physicalSwitchVOS.isEmpty())
                    reply.setError(Platform.argerr("Fail to get physical switch by mIP %s", mIP));

                result.setNodeUuid(physicalSwitchVOS.get(0).getNodeUuid());
            }

            reply.setInventories(OpenTSDBResultInventory.valueOf(results));
        } catch (Exception e) {
            reply.setError(Platform.argerr("Fail to query tunnel %s metric %s from OpenTSDB. Error: %s",
                    msg.getTunnelUuid(), msg.getMetrics(), e.getMessage()));
        }

        bus.reply(msg, reply);
    }


    private void handle(APICreateSpeedTestTunnelMsg msg) {
        SpeedTestTunnelVO vo = new SpeedTestTunnelVO();

        vo.setUuid(Platform.getUuid());
        vo.setTunnelUuid(msg.getTunnelUuid());

        vo = dbf.persistAndRefresh(vo);

        APICreateSpeedTestTunnelEvent event = new APICreateSpeedTestTunnelEvent(msg.getId());
        event.setInventory(SpeedTestTunnelInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIDeleteSpeedTestTunnelMsg msg) {
        SpeedTestTunnelVO vo = dbf.findByUuid(msg.getUuid(), SpeedTestTunnelVO.class);

        dbf.remove(vo);

        APIDeleteSpeedTestTunnelEvent event = new APIDeleteSpeedTestTunnelEvent(msg.getId());
        event.setInventory(SpeedTestTunnelInventory.valueOf(vo));

        bus.publish(event);
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
            throw new IllegalArgumentException("Failed to get monitor host!");

        return hostUuids.get(0);
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

        SwitchVO switchVO = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid, switchUuid)
                .find();

        if (switchVO == null)
            throw new IllegalArgumentException(String.format("Failed to get logical switch by switch port uuid %s!", switchPortUuid));

        return switchVO;
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

        PhysicalSwitchVO physicalSwitchVO = Q.New(PhysicalSwitchVO.class)
                .eq(PhysicalSwitchVO_.uuid, physicalSwitchUuid)
                .find();

        if (physicalSwitchVO == null)
            throw new IllegalArgumentException(String.format("Failed to get physical switch by switch port uuid %s!", switchPortUuid));

        return physicalSwitchVO;
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

        PhysicalSwitchVO physicalSwitchVO = Q.New(PhysicalSwitchVO.class)
                .eq(PhysicalSwitchVO_.uuid, physicalSwitchUuid)
                .find();

        if (physicalSwitchVO == null)
            throw new IllegalArgumentException(String.format("Failed to get physical switch by tunnel switch port uuid %s!", tunnelSwitchPortUuid));

        return physicalSwitchVO;
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

        if (vo == null)
            throw new IllegalArgumentException(String.format("Failed to get switch model %s!", uuid));

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
            throw new IllegalArgumentException(String.format("Failed to get host switch ref by physical switch %s!", physicalSwitchUuid));

        List<MonitorHostVO> monitorHostVOS = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.uuid, hostUuids.get(0))
                .list();

        if (monitorHostVOS.isEmpty())
            return null;

        return monitorHostVOS.get(0);
    }

    /***
     * 按节点、类型查找监控主机
     * @param nodeUuid
     * @param monitorType
     * @return
     */
    private MonitorHostVO getMonitorHostByNodeAndType(String nodeUuid, MonitorType monitorType) {
        List<MonitorHostVO> hosts = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.nodeUuid, nodeUuid)
                .eq(MonitorHostVO_.monitorType, monitorType)
                .eq(MonitorHostVO_.state, HostState.Enabled)
                .eq(MonitorHostVO_.status, HostStatus.Connected)
                .list();

        if (hosts.isEmpty())
            throw new IllegalArgumentException(String.format("Failed to get monitor by node %s and monitor type %s!", nodeUuid, monitorType));

        return hosts.get(0);
    }

    /**
     * 按nodeUuid与tunnelUuid查询监控通道
     *
     * @param
     * @return
     */
    public TunnelMonitorVO getTunnelMonitorByNodeAndTunnel(String nodeUuid, String tunnelUuid) {

        List<MonitorHostVO> host = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.nodeUuid, nodeUuid)
                .eq(MonitorHostVO_.monitorType, MonitorType.TUNNEL)
                .list();
        if (host.isEmpty())
            throw new ApiMessageInterceptionException(argerr("No Monitor Host exist under Node %s", nodeUuid));

        List<TunnelMonitorVO> tunnelMonitors = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid, tunnelUuid)
                .eq(TunnelMonitorVO_.hostUuid, host.get(0).getUuid()).list();
        if (tunnelMonitors.isEmpty())
            throw new ApiMessageInterceptionException(argerr("No Tunnel Monitor under tunnel: %s host:%s", tunnelUuid, host.get(0).getUuid()));

        return tunnelMonitors.get(0);
    }

    /**
     * 按switchPortUuid查询端点监控信息
     *
     * @param
     * @return
     */
    public TunnelMonitorVO getExistTunnelMonitorByTunnelSwitchPost(String switchPortUuid) {

        MonitorHostVO monitorHostVO = getHostBySwitchPort(switchPortUuid);

        List<TunnelMonitorVO> tunnelMonitorVO = Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.hostUuid, monitorHostVO.getUuid())
                .list();

        if (tunnelMonitorVO.isEmpty())
            throw new IllegalArgumentException(String.format("failed to get tunnel monitor by host %s", monitorHostVO.getUuid()));

        return tunnelMonitorVO.get(0);
    }

    public HostSwitchMonitorVO getHostSwitchMonitorByHostUuid(String hostUuid) {
        List<HostSwitchMonitorVO> vos = Q.New(HostSwitchMonitorVO.class)
                .eq(HostSwitchMonitorVO_.hostUuid, hostUuid)
                .list();

        if (vos.isEmpty())
            throw new IllegalArgumentException(String.format("Failed to get host switch ref by host %s!", hostUuid));


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
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateSpeedRecordsMsg) {
            validate((APICreateSpeedRecordsMsg) msg);
        } else if (msg instanceof APICreateSpeedTestTunnelMsg) {
            validate((APICreateSpeedTestTunnelMsg) msg);
        }

        return msg;
    }

    private void validate(APICreateSpeedRecordsMsg msg) {
        //测速tunnel是否有未完成的测速
        List<SpeedRecordsVO> vos = Q.New(SpeedRecordsVO.class)
                .eq(SpeedRecordsVO_.tunnelUuid, msg.getTunnelUuid())
                .eq(SpeedRecordsVO_.status, SpeedRecordStatus.TESTING)
                .list();

        if (vos.size() > 0) {
            long retryTime = TimeUnit.SECONDS.toMinutes(CoreGlobalProperty.RESET_SPEEDRECORD_INTERVAL);
            throw new ApiMessageInterceptionException(argerr("Uncompleted test records exists, please retry %s miniters later!", retryTime));
        }
    }

    private void validate(APICreateSpeedTestTunnelMsg msg) {
        SpeedTestTunnelVO vo = Q.New(SpeedTestTunnelVO.class).eq(SpeedTestTunnelVO_.tunnelUuid, msg.getTunnelUuid()).find();

        if (vo != null)
            throw new ApiMessageInterceptionException(argerr("Tunnel already exsited!"));

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(), TunnelVO.class);
        if (tunnelVO.getState() != TunnelState.Enabled)
            throw new ApiMessageInterceptionException(argerr("Tunnel %s is not enabled!", tunnelVO.getName()));

        if (tunnelVO.getMonitorState() != TunnelMonitorState.Enabled)
            throw new ApiMessageInterceptionException(argerr("Tunnel %s monitor state is not enabled!", tunnelVO.getName()));
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

    @Override
    public boolean start() {
        resetSpeedRecordStatus();

        evtf.registerSyncHttpCallHandler("FalconTunnel", MonitorAgentCommands.FalconGetTunnelCommand.class,
                cmd -> {

                    MonitorAgentCommands.FalconResponse falconResponse = new MonitorAgentCommands.FalconResponse();
                    try {
                        List<EndpointTunnelsInventory> tunnels = getEndpointTunnels(cmd);

                        falconResponse.setSuccess(true);
                        falconResponse.setMsg("success");
                        falconResponse.setInventories(tunnels);
                    } catch (Exception e) {
                        falconResponse.setSuccess(false);
                        falconResponse.setMsg(String.format("Get tunnels failed, Error: %s", e.getMessage()));
                    }

                    return JSONObjectUtil.toJsonString(falconResponse);
                });

        return true;
    }


    private void resetSpeedRecordStatus() {
        resetSpeedrecordInterval = CoreGlobalProperty.RESET_SPEEDRECORD_INTERVAL;
        expiredSpeedRecordTime = CoreGlobalProperty.EXPIRED_SPEEDRECORD_TIME;
        if (resetSpeedRecordStatusThread != null) {
            resetSpeedRecordStatusThread.cancel(true);
        }

        resetSpeedRecordStatusThread = thdf.submitPeriodicTask(new ResetSpeedRecordStatusThread(), 10);
    }

    private Future<Void> resetSpeedRecordStatusThread = null;
    private int resetSpeedrecordInterval;
    private int expiredSpeedRecordTime;

    private class ResetSpeedRecordStatusThread implements PeriodicTask {

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getInterval() {
            return TimeUnit.SECONDS.toSeconds(resetSpeedrecordInterval);
        }

        @Override
        public String getName() {
            return "reset-speedrecord-status-" + Platform.getManagementServerId();
        }

        private long getExpiredTime(Date createDate, int duration) {
            return createDate.getTime() + (expiredSpeedRecordTime + duration) * 1000;
        }

        @Override
        public void run() {
            try {
                logger.info(LocalTime.now() + ": 重置测速纪录状态");
                List<SpeedRecordsVO> list = Q.New(SpeedRecordsVO.class)
                        .eq(SpeedRecordsVO_.status, SpeedRecordStatus.TESTING)
                        .list();

                if (!list.isEmpty()) {
                    for (SpeedRecordsVO vo : list) {
                        long expiredTime = getExpiredTime(vo.getCreateDate(), vo.getDuration());
                        long currentTime = System.currentTimeMillis();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String d = format.format(expiredTime);
                        String c = format.format(currentTime);

                        if (System.currentTimeMillis() > expiredTime) {
                            vo.setStatus(SpeedRecordStatus.FAILURE);
                            dbf.update(vo);
                        }
                    }
                }
            } catch (Throwable t) {
                logger.warn("unhandled exception!");
            }
        }
    }

    /***
     * 按物理交换机mIP与vlan查找所有共点专线
     * @param cmd：查询你条件
     * @return：共点专线清单
     */
    private List<EndpointTunnelsInventory> getEndpointTunnels(MonitorAgentCommands.FalconGetTunnelCommand cmd) {
        List<EndpointTunnelsInventory> tunnels = new ArrayList<>();

        // 按物理交换机IP获取交换机端口
        Set<String> ports = getPortsByPhysicalSwitch(cmd.getPhysicalSwitchMip());

        // 按交换机端口、vlan获取tunnel
        List<MonitorAgentCommands.EndpointTunnel> endpointTunnels = getTunnelBySwitchAndVlan(ports, cmd.getPhysicalSwitchMip(), cmd.getVlan());

        return EndpointTunnelsInventory.valueOf(endpointTunnels);
    }

    private List<MonitorAgentCommands.EndpointTunnel> getTunnelBySwitchAndVlan(Set<String> ports, String physicalSwitchMip, Integer vlan) {
        List<MonitorAgentCommands.EndpointTunnel> endpointTunnels = new ArrayList<>();

        for (String portUuid : ports) {
            List<TunnelSwitchPortVO> portTunnels = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.switchPortUuid, portUuid)
                    .eq(TunnelSwitchPortVO_.vlan, vlan).list();

            if (portTunnels.isEmpty())
                continue;

            for (TunnelSwitchPortVO portTunnel : portTunnels) {
                TunnelVO tunnelVO = new TunnelVO();
                tunnelVO = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, portTunnel.getTunnelUuid()).find();

                MonitorAgentCommands.EndpointTunnel endpointTunnel = new MonitorAgentCommands.EndpointTunnel();
                for (TunnelSwitchPortVO tunnelSwitchPort : tunnelVO.getTunnelSwitchPortVOS()) {
                    if (tunnelSwitchPort.getSortTag().equals(InterfaceType.A.toString())) {
                        endpointTunnel.setNodeA(tunnelSwitchPort.getEndpointVO().getNodeVO().getName());
                        endpointTunnel.setEndpoingAMip(getPhysicalSwitchBySwitchPort(tunnelSwitchPort.getSwitchPortUuid()).getmIP());
                    } else if (tunnelSwitchPort.getSortTag().equals(InterfaceType.Z.toString())) {
                        endpointTunnel.setNodeZ(tunnelSwitchPort.getEndpointVO().getNodeVO().getName());
                        endpointTunnel.setEndpoingZMip(getPhysicalSwitchBySwitchPort(tunnelSwitchPort.getSwitchPortUuid()).getmIP());
                    }
                }

                endpointTunnel.setTunnelUuid(tunnelVO.getUuid());
                endpointTunnel.setTunnelName(tunnelVO.getName());
                endpointTunnel.setBandwidth(tunnelVO.getBandwidth());
                endpointTunnel.setAccountUuid(tunnelVO.getAccountUuid());

                endpointTunnels.add(endpointTunnel);
            }
        }

        if (endpointTunnels.isEmpty())
            throw new IllegalArgumentException(String.format("No tunnel exist under endpoint %s vlan %s ", physicalSwitchMip, vlan));

        return endpointTunnels;
    }

    private Set<String> getPortsByPhysicalSwitch(String physicalSwitchMip) {
        // 查询物理交换机
        List<PhysicalSwitchVO> physicalSwitchVOS = Q.New(PhysicalSwitchVO.class).eq(PhysicalSwitchVO_.mIP, physicalSwitchMip).list();
        if (physicalSwitchVOS.isEmpty())
            throw new IllegalArgumentException(String.format("No physicalSwitch exist under endpoint %s!", physicalSwitchMip));

        // 查询逻辑交换机
        List<SwitchVO> switchVOS = Q.New(SwitchVO.class).eq(SwitchVO_.physicalSwitchUuid, physicalSwitchVOS.get(0).getUuid()).list();
        if (switchVOS.isEmpty())
            throw new IllegalArgumentException(String.format("No logical switch exist under physical switch %s!", physicalSwitchVOS.get(0).getCode()));

        // 逻辑交换机端口
        List<String> portVOS = Q.New(SwitchPortVO.class).eq(SwitchPortVO_.switchUuid, switchVOS.get(0).getUuid()).select(SwitchPortVO_.uuid).list();
        // List<String> portVOS =  Q.New(SwitchVO.class).eq(SwitchVO_.physicalSwitchUuid,physicalSwitchVOS.get(0).getUuid()).select(SwitchVO_.uuid).list();
        if (portVOS.isEmpty())
            throw new IllegalArgumentException(String.format("No switch port exist under logical switch %s!", switchVOS.get(0).getCode()));

        Set<String> portsSet = new HashSet();
        for (String portUuid : portVOS)
            portsSet.add(portUuid);

        return portsSet;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(MonitorConstant.SERVICE_ID);
    }
}
