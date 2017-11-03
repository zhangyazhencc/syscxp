package com.syscxp.tunnel.manage;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.db.*;
import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.falconapi.FalconApiCommands;
import com.syscxp.header.falconapi.FalconApiRestConstant;
import com.syscxp.header.host.HostVO;
import com.syscxp.tunnel.header.host.*;
import com.syscxp.tunnel.header.monitor.*;
import com.syscxp.tunnel.header.switchs.*;
import com.syscxp.tunnel.header.tunnel.TunnelSwitchPortVO;
import com.syscxp.tunnel.header.tunnel.TunnelSwitchPortVO_;
import com.syscxp.utils.gson.JSONObjectUtil;
import jdk.nashorn.internal.ir.ReturnNode;
import org.apache.commons.collections.ListUtils;
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
import com.syscxp.tunnel.header.tunnel.TunnelVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;

import javax.management.monitor.Monitor;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.syscxp.core.Platform.argerr;
import static com.syscxp.core.Platform.operr;

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

    private void handle(APIStartTunnelMonitorMsg msg) {
        APIStartTunnelMonitorEvent event = new APIStartTunnelMonitorEvent(msg.getId());
        boolean success = true;

        // 创建监控通道
        List<TunnelMonitorVO> tunnelMonitorVOS = createTunnelMonitorHandle(msg);

        // 控制器命令下发
        ControllerCommands.ControllerRestResponse controllerResponse = issueControllerCommand(msg.getTunnelUuid(),tunnelMonitorVOS);
        //TODO:修改ControllerCommands.ControllerRestResponse，需要保存message
        if(!controllerResponse.isSuccess()){
            event.setSuccess(false);
            event.setError(Platform.operr("监控命令下发失败！"));
            success = false;
        }

        // 同步icmp
        if(success){
            FalconApiCommands.RestResponse falconResponse = icmpSync(msg);
            if(!falconResponse.isSuccess()){
                event.setSuccess(false);
                event.setError(Platform.operr("监控命令下发失败！"));
                success = false;
            }
        }

        if(success){
            // TODO:更新TunnelVO cidr/monitorState
        }

        // event.setInventories(TunnelMonitorInventory.valueOf(tunnelMonitorVOS));
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
    @Transactional
    public List<TunnelMonitorVO> createTunnelMonitorHandle(APIStartTunnelMonitorMsg msg) {
        List<TunnelMonitorVO> tunnelMonitorVOS = new ArrayList<TunnelMonitorVO>();
        try{
            // 按tunnel查找TunnelMonitorVO已经存在的IP
            List<String> locatedIps =
                    Q.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelUuid, msg.getTunnelUuid()).select(TunnelMonitorVO_.monitorIp).findValue();

            // 获取tunnel两端交换机端口
            List<TunnelSwitchPortVO> portVOS = new ArrayList<TunnelSwitchPortVO>(2);

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
                String monitorIp = getMonitorIp(msg.getMonitorCidr(),msg.getStartIp(), msg.getEndIp(), locatedIps);

                monitorVO.setUuid(Platform.getUuid());
                monitorVO.setTunnelUuid(msg.getTunnelUuid());
                monitorVO.setTunnelSwitchPortUuid(tunnelSwitchPortVO.getUuid());
                monitorVO.setHostUuid(hostUuid);
                monitorVO.setMonitorIp(monitorIp);
                dbf.getEntityManager().persist(monitorVO);

                tunnelMonitorVOS.add(monitorVO);
            }
            dbf.getEntityManager().flush();
        }catch (Exception e){
            throw new OperationFailureException(operr("创建监控通道失败！:" + e.getMessage()));
        }

        if(tunnelMonitorVOS.isEmpty())
            throw new OperationFailureException(operr("创建监控通道失败！"));
        return tunnelMonitorVOS;
    }

    /***
     * 监控命令下发至控制器
     * @param tunnelUuid
     * @param tunnelMonitorVOS
     * @return
     */
    private ControllerCommands.ControllerRestResponse issueControllerCommand(String tunnelUuid,List<TunnelMonitorVO> tunnelMonitorVOS){
        ControllerCommands.ControllerRestResponse response = new ControllerCommands.ControllerRestResponse();

        ControllerCommands.TunnelMonitorCommand cmd = getTunnelMonitorCommand(tunnelUuid);
        try{
            String command = JSONObjectUtil.toJsonString(cmd);
            String url = CoreGlobalProperty.CONTROLLER_MANAGER_URL;

            try {
                response = evtf.syncJsonPost(url + ControllerRestConstant.START_TUNNEL_MONITOR, command, ControllerCommands.ControllerRestResponse.class);
            } catch (Exception e) {
                response.setCode("1");
            }
        }catch (Exception e){
            throw new OperationFailureException(operr("下发监控命令至控制器失败！:" + e.getMessage()));
        }

        return response;
    }

    /**
     * 开启监控、tunnel修改且监控为开启状态时同步ICMP到falcon_portal数据库
     *
     * @param msg
     * @return：创建的监控通道
     */
    private FalconApiCommands.RestResponse icmpSync(APIStartTunnelMonitorMsg msg) {
        //TODO: 重写数据获取方法
        FalconApiCommands.Icmp icmp = new FalconApiCommands.Icmp();
        icmp.setTunnel_id("5536731d5fff489c88ad62636ac0xxxx");
        icmp.setTunnel_name("xx专线");
        icmp.setUser_id("5536731d5fff489c88ad62636ac0dfhg");
        icmp.setCidr("172.16.0.0/24");
        icmp.setEndpointA_ip("192.168.211.11");
        icmp.setEndpointB_ip("192.168.211.13");
        icmp.setEndpointA_vid(4057);
        icmp.setEndpointB_vid(4988);
        icmp.setEndpointA_mip("192.168.1.253/30");
        icmp.setEndpointB_mip("192.168.1.254/30");
        icmp.setEndpointA_id("8836731d5fff489c88ad62636ac0baid");
        icmp.setEndpointB_id("8836731d5fff489c88ad62636ac0bdib");
        icmp.setEndpointA_interface("enp2s0");
        icmp.setEndpointB_interface("enp2s1");
        icmp.setHostA_ip("192.168.211.23");
        icmp.setHostB_ip("192.168.211.24");

        //FalconApiRestFacade farf = new FalconApiRestFacade();
        //FalconApiCommands.RestResponse response = farf.syncRequest(FalconApiRestConstant.ICMP_SYNC,body);
        String url = CoreGlobalProperty.FALCON_API_SERVER_URL;

        FalconApiCommands.RestResponse response = new FalconApiCommands.RestResponse();
        try {
            response = evtf.syncJsonPost(url + FalconApiRestConstant.ICMP_SYNC, JSONObjectUtil.toJsonString(icmp), FalconApiCommands.RestResponse.class);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMsg(String.format("unable to post %s. %s", url, e.getMessage()));
        }

        return response;
    }

    /**
     * 获取监控下发controller命令
     *
     * @param tunnelUuid
     * @return
     */
    private ControllerCommands.TunnelMonitorCommand getTunnelMonitorCommand(String tunnelUuid) {
        List<ControllerCommands.TunnelMonitorMpls> mplsList = new ArrayList<>();
        List<ControllerCommands.TunnelMonitorSdn> sdnList = new ArrayList<>();

        // 获取两端监控IP与端口
        Map<String, String> monitorIp = new HashMap<>();
        Map<String, String> monitorPort = new HashMap<>();
        getIpPort(tunnelUuid, monitorIp, monitorPort);

        //根据tunnel获取两端监控主机与物理接口
        String monitorSql = "select b.interfaceType,b.hostUuid,b.monitorIp,b.interfaceUuid\n" +
                "from TunnelMonitorVO a,TunnelMonitorInterfaceVO b\n" +
                "where b.tunnelMonitorUuid = a.uuid\n" +
                "and a.tunnelUuid = :tunnelUuid";
        TypedQuery<Tuple> monitorQ = dbf.getEntityManager().createQuery(monitorSql, Tuple.class);
        monitorQ.setParameter("tunnelUuid", tunnelUuid);
        for (Tuple monitor : monitorQ.getResultList()) {
            // 获取交换机信息
            String hostSql = "select e.accessType,e.mIP,e.username,e.password,f.model,f.subModel,d.physicalSwitchPortName,d.physicalSwitchUuid\n" +
                    "from HostVO c,HostSwitchMonitorVO d, PhysicalSwitchVO e,SwitchModelVO f\n" +
                    "where d.hostUuid = c.uuid\n" +
                    "and d.physicalSwitchUuid = e.uuid\n" +
                    "and f.uuid = e.switchModelUuid\n" +
                    "and c.uuid = :hostUuid";
            TypedQuery<Tuple> hostQ = dbf.getEntityManager().createQuery(hostSql, Tuple.class);
            hostQ.setParameter("hostUuid", monitor.get(1).toString());
            Tuple host = hostQ.getResultList().get(0);

            String tunnelSql = "select g.bandwidth,h.vlan,j.portName,g.vsi \n" +
                    "from TunnelVO g, TunnelInterfaceVO h, InterfaceVO i, SwitchPortVO j\n" +
                    "where h.tunnelUuid = g.uuid\n" +
                    "and h.sortTag = :sortTag\n" +
                    "and i.uuid = h.interfaceUuid\n" +
                    "and j.uuid = i.switchPortUuid\n" +
                    "and g.uuid = :tunnelUuid";
            TypedQuery<Tuple> tunnelQ = dbf.getEntityManager().createQuery(tunnelSql, Tuple.class);
            tunnelQ.setParameter("tunnelUuid", tunnelUuid);
            tunnelQ.setParameter("sortTag", monitor.get(0).toString());
            Tuple tunnel = tunnelQ.getResultList().get(0);

            ControllerCommands.TunnelMonitorMpls mpls = new ControllerCommands.TunnelMonitorMpls();
            if (PhysicalSwitchAccessType.MPLS.toString().equals(host.get(0).toString())) {
                mpls.setM_ip(host.get(1).toString());
                mpls.setUsername(host.get(2).toString());
                mpls.setPassword(host.get(3).toString());
                mpls.setSwitch_type(host.get(4).toString());
                mpls.setSub_type(host.get(5).toString());
                mpls.setVlan_id(Integer.valueOf(tunnel.get(1).toString()) + 1);
                mpls.setPort_name(tunnel.get(2, String.class));
                mpls.setBandwidth(Integer.valueOf(tunnel.get(0).toString()));
                mpls.setVni(Integer.valueOf(tunnel.get(3).toString()));
                mplsList.add(mpls);
            } else if (PhysicalSwitchAccessType.SDN.toString().equals(host.get(0).toString())) {
                // 获取上联口对应的物理交换机作为mpls数据
                String uplinkSql = "select b.accessType,b.mIP,b.username,b.password,c.model,c.subModel,a.uplinkPhysicalSwitchPortName as physicalSwitchPortName\n" +
                        "from PhysicalSwitchUpLinkRefVO a, PhysicalSwitchVO b,SwitchModelVO c\n" +
                        "where b.uuid = a.uplinkPhysicalSwitchUuid\n" +
                        "and c.uuid = b.switchModelUuid\n" +
                        "and a.physicalSwitchUuid = :physicalSwitchUuid";
                TypedQuery<Tuple> uplinkQ = dbf.getEntityManager().createQuery(uplinkSql, Tuple.class);
                uplinkQ.setParameter("physicalSwitchUuid", host.get(7).toString());
                Tuple uplink = uplinkQ.getResultList().get(0);

                mpls.setM_ip(uplink.get(1).toString());
                mpls.setUsername(uplink.get(2).toString());
                mpls.setPassword(uplink.get(3).toString());
                mpls.setSwitch_type(uplink.get(4).toString());
                mpls.setSub_type(uplink.get(5).toString());
                mpls.setVlan_id(Integer.valueOf(tunnel.get(1).toString()) + 1);
                mpls.setPort_name(tunnel.get(2, String.class));
                // mpls.setBandwidth(Integer.valueOf(tunnel.get(0).toString()));

                mplsList.add(mpls);

                ControllerCommands.TunnelMonitorSdn sdn = new ControllerCommands.TunnelMonitorSdn();
                sdn.setM_ip(host.get(1, String.class));
                if (monitor.get(0).toString().equals(InterfaceType.A.toString())) {
                    sdn.setNw_src(monitorIp.get(InterfaceType.A.toString()));
                    sdn.setNw_dst(monitorIp.get(InterfaceType.Z.toString()));
                    sdn.setIn_port(monitorPort.get(InterfaceType.A.toString()));
                    sdn.setUplink(uplink.get(6).toString());
                }
                if (monitor.get(0).toString().equals(InterfaceType.Z.toString())) {
                    sdn.setNw_src(monitorIp.get(InterfaceType.Z.toString()));
                    sdn.setNw_dst(monitorIp.get(InterfaceType.A.toString()));
                    sdn.setIn_port(monitorPort.get(InterfaceType.Z.toString()));
                    sdn.setUplink(uplink.get(6).toString());
                }
                sdn.setBandwidth(Integer.valueOf(tunnel.get(0).toString()));
                sdn.setVlan_id(Integer.valueOf(tunnel.get(1).toString()) + 1);

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

        if(hostUuids.isEmpty())
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
    private String getMonitorIp(String cidr,String startIp, String endIp, List<String> locatedIps) {
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

        return ip+mask;
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
     * 按tunnelUuid删除TunnelMonitorVO
     * @param tunnelUuid
     */
    public void deleteTunnelMonitorByTunnel(String tunnelUuid){
        List<TunnelMonitorVO> vos = Q.New(TunnelMonitorVO.class).
                eq(TunnelMonitorVO_.tunnelUuid,tunnelUuid).
                list();

        for(TunnelMonitorVO vo:vos){
            dbf.getEntityManager().remove(vo);
        }
    }

    /***
     * 按tunnelSwitchPortUuid修改unnelMonitorVO
     * @param tunnelUuid
     */
    public void updateTunnelMonitorHostByTunnelSwitchPort(String tunnelSwitchPortUuid){
        TunnelSwitchPortVO tunnelSwitchPortVO = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.uuid,tunnelSwitchPortUuid)
                .find();

        MonitorHostVO monitorHostVO = getHostBySwitchPort(tunnelSwitchPortVO.getSwitchPortUuid());
        TunnelMonitorVO tunnelMonitorVO = getTunnelMonitorByTunnelSwitchPort(tunnelSwitchPortUuid);

        if(tunnelMonitorVO!=null){
            if(monitorHostVO!=null){
                tunnelMonitorVO.setHostUuid(monitorHostVO.getUuid());

                dbf.update(tunnelMonitorVO);
            }
        }
    }

    /***
     * 按switchPortUuid查询SwitchVO
     * @param switchPortUuid
     * @return
     */
    public SwitchVO getSwitchBySwitchPort(String switchPortUuid){
        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid,switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        return Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid,switchUuid)
                .find();
    }

    /***
     * 按switchPortUuid查询PhysicalSwitchVO
     * @param switchPortUuid
     * @return
     */
    public PhysicalSwitchVO getPhysicalSwitchBySwitchPort(String switchPortUuid){
        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid,switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid,switchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();

        return Q.New(PhysicalSwitchVO.class)
                .eq(PhysicalSwitchVO_.uuid,physicalSwitchUuid)
                .find();
    }

    /***
     * 按switchPortUuid查询监控Host
     * @param switchPortUuid
     * @return
     */
    public MonitorHostVO getHostBySwitchPort(String switchPortUuid){
        String switchUuid = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid,switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid,switchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();

        List<String> hostUuids = Q.New(HostSwitchMonitorVO.class)
                .eq(HostSwitchMonitorVO_.physicalSwitchUuid,physicalSwitchUuid)
                .select(HostSwitchMonitorVO_.hostUuid)
                .list();

        if(hostUuids.isEmpty())
            return null;

        List<MonitorHostVO> monitorHostVOS = Q.New(MonitorHostVO.class)
                .eq(MonitorHostVO_.uuid,hostUuids.get(0))
                .list();

        if(monitorHostVOS.isEmpty())
            return null;

        return monitorHostVOS.get(0);
    }

    /***
     * 按tunnelUuid查询监控通道
     * @param tunnelUuid
     * @return
     */
    public List<TunnelMonitorVO> getTunnelMonitorByTunnel(String tunnelUuid){
        return Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelUuid,tunnelUuid)
                .list();
    }

    /***
     * 按tunnelSwitchPortUuid查询监控通道
     * @param tunnelUuid
     * @return
     */
    public TunnelMonitorVO getTunnelMonitorByTunnelSwitchPort(String tunnelSwitchPortUuid){
        return Q.New(TunnelMonitorVO.class)
                .eq(TunnelMonitorVO_.tunnelSwitchPortUuid,tunnelSwitchPortUuid)
                .find();
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
