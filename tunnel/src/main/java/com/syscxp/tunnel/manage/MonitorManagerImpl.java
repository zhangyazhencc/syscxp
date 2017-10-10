package com.syscxp.tunnel.manage;

import com.syscxp.tunnel.header.host.*;
import com.syscxp.tunnel.header.monitor.*;
import com.syscxp.tunnel.header.switchs.PhysicalSwitchVO;
import com.syscxp.tunnel.header.tunnel.TunnelInterfaceVO_;
import com.syscxp.tunnel.sdk.sdn.service.RyuRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.cloudbus.ResourceDestinationMaker;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.tunnel.header.tunnel.NetworkVO;
import com.syscxp.tunnel.header.tunnel.TunnelInterfaceVO;
import com.syscxp.tunnel.header.tunnel.TunnelVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;

import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        TunnelMonitorVO tunnelMonitorVO = createTunnelMonitorHandle(msg);

        // 下发监控通道配置
        RyuRestService ryuRestService = new RyuRestService();
        ryuRestService.tunnelMonitorIssue(tunnelMonitorVO.getTunnelUuid(),msg);

        // 测试
        /*
        TunnelMonitorVO tunnelMonitorVO = new TunnelMonitorVO();
        tunnelMonitorVO.setUuid(Platform.getUuid());
        tunnelMonitorVO.setTunnelUuid(Platform.getUuid());
        RyuRestService ryuRestService = new RyuRestService();
        ryuRestService.restTest(tunnelMonitorVO.getTunnelUuid(),msg);
        */

        APICreateTunnelMonitorEvent event = new APICreateTunnelMonitorEvent(msg.getId());
        event.setInventory(TunnelMonitorInventory.valueOf(tunnelMonitorVO));
        bus.publish(event);
    }

    private void handle(APIUpdateTunnelMonitorMsg msg) {
        TunnelMonitorVO tunnelMonitorVO = dbf.findByUuid(msg.getUuid(), TunnelMonitorVO.class);

        tunnelMonitorVO.setTunnelUuid(msg.getTunnelUuid());
        tunnelMonitorVO.setStatus(msg.getStatus());
        if (msg.getMsg() != null) {
            tunnelMonitorVO.setMsg(msg.getMsg());
        }
        dbf.getEntityManager().merge(tunnelMonitorVO);

        List<TunnelMonitorInterfaceVO> interfaceVOList = tunnelMonitorVO.getTunnelMonitorInterfaceVOList();

        TunnelMonitorInterfaceVO interfaceVO = new TunnelMonitorInterfaceVO();
        for (TunnelMonitorInterfaceVO vo : interfaceVOList) {
            if (InterfaceType.A.equals(vo.getInterfaceType())) {
                interfaceVO.setMonitorIp(msg.getMonitorAIp());
                interfaceVO.setHostUuid(msg.getHostAUuid());
                dbf.getEntityManager().merge(interfaceVO);
            } else if (InterfaceType.Z.equals(vo.getInterfaceType())) {
                interfaceVO.setMonitorIp(msg.getMonitorZIp());
                interfaceVO.setHostUuid(msg.getHostZUuid());
                dbf.getEntityManager().merge(interfaceVO);
            }
        }

        TunnelMonitorVO resultVo = dbf.findByUuid(msg.getUuid(), TunnelMonitorVO.class);
        APIUpdateTunnelMonitorEvent evt = new APIUpdateTunnelMonitorEvent(msg.getId());
        evt.setInventory(TunnelMonitorInventory.valueOf(resultVo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APIDeleteTunnelMonitorMsg msg) {
        String uuid = msg.getUuid();
        TunnelMonitorVO tunnelMonitorVO = dbf.findByUuid(msg.getUuid(), TunnelMonitorVO.class);

        dbf.getEntityManager().remove(tunnelMonitorVO);
        for (TunnelMonitorInterfaceVO vo : tunnelMonitorVO.getTunnelMonitorInterfaceVOList()) {
            dbf.remove(vo);
        }

        APIDeleteTunnelMonitorEvent event = new APIDeleteTunnelMonitorEvent(msg.getId());
        event.setInventory(TunnelMonitorInventory.valueOf(tunnelMonitorVO));

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

    /**
     * 创建监控通道 （手动创建 / 创建tunnel时自动创建）
     *
     * @param msg
     * @return：创建的监控通道
     */
    @Transactional
    public TunnelMonitorVO createTunnelMonitorHandle(APICreateTunnelMonitorMsg msg) {
        // TunnelMonitorVO
        TunnelMonitorVO tunnelMonitorVO = new TunnelMonitorVO();
        tunnelMonitorVO.setUuid(Platform.getUuid());
        tunnelMonitorVO.setTunnelUuid(msg.getTunnelUuid());
        tunnelMonitorVO.setStatus(TunnelMonitorStatus.APPLYING);
        tunnelMonitorVO.setMsg(msg.getMsg());
        dbf.getEntityManager().persist(tunnelMonitorVO);

        Map<String, String> tunnelInterfaces = new HashMap<String, String>(2);
        Map<String, String> monitorIps = new HashMap<String, String>(2);
        Map<String, String> monitorHosts = new HashMap<String, String>(2);
        // 按tunnel查询两端接口是否已存在监控机与监控IP
        List<TunnelMonitorInterfaceVO> existedMonitorList = getExistedMonitor(tunnelMonitorVO.getTunnelUuid());
        for (TunnelMonitorInterfaceVO existedMonitor : existedMonitorList) {
            tunnelInterfaces.put(existedMonitor.getInterfaceType().toString(), existedMonitor.getInterfaceUuid());
            monitorIps.put(existedMonitor.getInterfaceType().toString(), existedMonitor.getMonitorIp());
            monitorHosts.put(existedMonitor.getInterfaceType().toString(), existedMonitor.getHostUuid());
        }

        // 按tunnel监控IP段随机产生
        generateMonirotIps(tunnelMonitorVO.getTunnelUuid(), monitorIps);
        // 按tunnel查询两端监控机
        getMonirotHost(tunnelMonitorVO.getTunnelUuid(), monitorHosts);
        // 按tunnel查询tunnel两端接口
        getTunnelInterface(tunnelMonitorVO.getTunnelUuid(), tunnelInterfaces);

        // TunnelMonitorInterfaceVO(A)
        TunnelMonitorInterfaceVO interfaceA = new TunnelMonitorInterfaceVO();
        interfaceA.setUuid(Platform.getUuid());
        interfaceA.setInterfaceType(InterfaceType.A);
        interfaceA.setTunnelMonitorUuid(tunnelMonitorVO.getUuid());
        interfaceA.setInterfaceUuid(tunnelInterfaces.get(InterfaceType.A.toString()));
        interfaceA.setHostUuid(monitorHosts.get(InterfaceType.A.toString()));
        interfaceA.setMonitorIp(monitorIps.get(InterfaceType.A.toString()));
        dbf.getEntityManager().persist(interfaceA);

        // TunnelMonitorInterfaceVO(Z)
        TunnelMonitorInterfaceVO interfaceZ = new TunnelMonitorInterfaceVO();
        interfaceZ.setUuid(Platform.getUuid());
        interfaceZ.setInterfaceType(InterfaceType.Z);
        interfaceZ.setTunnelMonitorUuid(tunnelMonitorVO.getUuid());
        interfaceZ.setInterfaceUuid(tunnelInterfaces.get(InterfaceType.Z.toString()));
        interfaceZ.setHostUuid(monitorHosts.get(InterfaceType.Z.toString()));
        interfaceZ.setMonitorIp(monitorIps.get(InterfaceType.Z.toString()));
        dbf.getEntityManager().persist(interfaceZ);

        dbf.getEntityManager().flush();

        return tunnelMonitorVO;
    }

    /**
     * 按tunnel查找两端接口是否有监控机与监控IP
     *
     * @param tunnelUuid
     * @return 监控列表
     */
    private List<TunnelMonitorInterfaceVO> getExistedMonitor(String tunnelUuid) {
        String sql = "select c from TunnelVO a,TunnelInterfaceVO b,TunnelMonitorInterfaceVO c\n" +
                "where a.uuid = :tunnelUuid\n" +
                "and b.tunnelUuid = a.uuid\n" +
                "and c.interfaceUuid = b.interfaceUuid";

        TypedQuery<TunnelMonitorInterfaceVO> query = dbf.getEntityManager().createQuery(sql, TunnelMonitorInterfaceVO.class);
        query.setParameter("tunnelUuid", tunnelUuid);
        List<TunnelMonitorInterfaceVO> list = query.getResultList();

        return list;
    }

    /**
     * 按tunnel监控IP段随机生成监控IP
     *
     * @param tunnelUuid
     * @return：监控A/Z两端监控IP
     */
    private void generateMonirotIps(String tunnelUuid, Map<String, String> monitorIps) {
        NetworkVO network;
        TunnelVO tunnel;
        MonitorCidrVO monitorCidr;

        // A/Z至少有一个不存在
        if (!monitorIps.containsKey(InterfaceType.A.toString()) || !monitorIps.containsKey(InterfaceType.Z.toString())) {
            tunnel = dbf.findByUuid(tunnelUuid, TunnelVO.class);
            if (tunnel != null) {
                network = dbf.findByUuid(tunnel.getNetworkUuid(), NetworkVO.class);

                if (network != null) {
                    SimpleQuery<MonitorCidrVO> qMonitorCidr = dbf.createQuery(MonitorCidrVO.class);
                    qMonitorCidr.add(MonitorCidrVO_.monitorCidr, SimpleQuery.Op.EQ, network.getMonitorCidr());
                    monitorCidr = qMonitorCidr.find();

                    if (monitorCidr != null) {
                        //TODO: 性能优化
                        // vsi下已有监控ip
                        String sqlExistedMonitorIp = "select c.monitorIp " +
                                "from TunnelVO a,TunnelMonitorVO b,TunnelMonitorInterfaceVO c " +
                                "where a.networkUuid = :networkUuid " +
                                "and b.tunnelUuid = a.uuid " +
                                "and c.tunnelMonitorUuid = b.uuid";
                        TypedQuery<String> qExistedMonitorIp = dbf.getEntityManager().createQuery(sqlExistedMonitorIp, String.class);
                        qExistedMonitorIp.setParameter("networkUuid", network.getUuid());
                        List<String> existedMonitorIpList = qExistedMonitorIp.getResultList();

                        // 当前vsi的监控IP段
                        String[] startIps = monitorCidr.getStartAddress().split("\\.");
                        String[] endIps = monitorCidr.getEndAddress().split("\\.");
                        String prefix = startIps[0] + "." + startIps[1] + ".";

                        // 生成IP
                        if (!monitorIps.containsKey(InterfaceType.A.toString())) {
                            String IpA = recursiveGenerateIp(existedMonitorIpList, startIps, endIps, prefix);
                            monitorIps.put(InterfaceType.A.toString(), IpA);
                        }
                        if (!monitorIps.containsKey(InterfaceType.Z.toString())) {
                            String IpZ = recursiveGenerateIp(existedMonitorIpList, startIps, endIps, prefix);
                            monitorIps.put(InterfaceType.Z.toString(), IpZ);
                        }
                    }
                }
            }
        }

        if (monitorIps == null) {
            throw new IllegalArgumentException("monitor ip generator error!");
        }
    }

    /**
     * 递归校验并生成IP
     *
     * @param existIpList:已存在的ip列表
     * @param startIps：起始IP
     * @param endIps：结束IP
     * @param prefix：IP前缀
     * @return：生成IP
     */
    private String recursiveGenerateIp(List<String> existIpList, String[] startIps, String[] endIps, String prefix) {
        String result;

        String ip2 = String.valueOf(randonCommon(Integer.parseInt(startIps[2]), Integer.parseInt(endIps[2])));
        String ip3 = String.valueOf(randonCommon(Integer.parseInt(startIps[3]), Integer.parseInt(endIps[3])));
        result = prefix + ip2 + "." + ip3;

        if (existIpList.contains(result)) {
            recursiveGenerateIp(existIpList, startIps, endIps, prefix);
        }
        existIpList.add(result);

        return result;
    }

    /**
     * 按tunnel查询两端监控主机
     *
     * @param tunnelUuid
     * @return：监控A/Z两端监控主机
     */
    private void getMonirotHost(String tunnelUuid, Map<String, String> monitorHosts) {
        if (!monitorHosts.containsKey(InterfaceType.A.toString()) || !monitorHosts.containsKey(InterfaceType.Z.toString())) {

            List<TunnelInterfaceVO> tunnelInterfaces = getTunnelInterfaces(tunnelUuid);
            for (TunnelInterfaceVO tunnelInterface : tunnelInterfaces) {
                if (!monitorHosts.containsKey(tunnelInterface.getSortTag())) {
                    String sqlPhysicalSwitchUuid = "select c.physicalSwitchUuid from InterfaceVO a,SwitchPortVO b,SwitchVO c\n " +
                            "where b.uuid = a.switchPortUuid\n " +
                            "and c.uuid = b.switchUuid\n " +
                            "and a.uuid = :interfaceUuid";
                    TypedQuery<String> qPhysicalSwitchUuid = dbf.getEntityManager().createQuery(sqlPhysicalSwitchUuid, String.class);
                    qPhysicalSwitchUuid.setParameter("interfaceUuid", tunnelInterface.getInterfaceUuid());

                    List<String> physicalSwitchUuidlist = qPhysicalSwitchUuid.getResultList();
                    if (physicalSwitchUuidlist.size() > 0) {
                        String physicalSwitchUuid = physicalSwitchUuidlist.get(0);

                        String sqlHostUuid = "select b.uuid from HostSwitchMonitorVO a,HostVO b\n" +
                                "where b.uuid = a.hostUuid\n" +
                                "and a.physicalSwitchUuid = :physicalSwitchUuid";
                        TypedQuery<String> qHostUuid = dbf.getEntityManager().createQuery(sqlHostUuid, String.class);
                        qHostUuid.setParameter("physicalSwitchUuid", physicalSwitchUuid);

                        List<String> hostUuidlist = qHostUuid.getResultList();
                        if(hostUuidlist.size()<=0){
                            throw new IllegalArgumentException("no host releated to physical switch!");
                        }
                        // 物理监控机对应多台监控机，则随机分配一台
                        int index = (int) (Math.random() * hostUuidlist.size());
                        String hostUuid = hostUuidlist.get(index);

                        if (InterfaceType.A.toString().equals(tunnelInterface.getSortTag()))
                            monitorHosts.put(InterfaceType.A.toString(), hostUuid);
                        else if (InterfaceType.Z.toString().equals(tunnelInterface.getSortTag()))
                            monitorHosts.put(InterfaceType.Z.toString(), hostUuid);
                    }
                }
            }
        }

        if (monitorHosts == null) {
            throw new IllegalArgumentException("cannot get any monitor host!");
        }
    }

    /**
     * 按tunnel获取两端接口
     *
     * @param tunnelUuid
     * @return：Tunnel A/Z两端接口
     */
    private void getTunnelInterface(String tunnelUuid, Map<String, String> tunnelInterfaces) {

        if (!tunnelInterfaces.containsKey(InterfaceType.A.toString()) || !tunnelInterfaces.containsKey(InterfaceType.Z.toString())) {
            List<TunnelInterfaceVO> tunnelInterfaceList = getTunnelInterfaces(tunnelUuid);
            for (TunnelInterfaceVO tunnelInterface : tunnelInterfaceList) {
                if (!tunnelInterfaces.containsKey(tunnelInterface.getSortTag())) {
                    tunnelInterfaces.put(tunnelInterface.getSortTag(), tunnelInterface.getInterfaceUuid());
                }
            }
        }

        if (tunnelInterfaces == null) {
            throw new IllegalArgumentException("cannot get any monitor tunnel interface!");
        }
    }

    private List<TunnelInterfaceVO> getTunnelInterfaces(String tunnelUuid) {
        SimpleQuery<TunnelInterfaceVO> qTunnelInterface = dbf.createQuery(TunnelInterfaceVO.class);
        qTunnelInterface.add(TunnelInterfaceVO_.tunnelUuid, SimpleQuery.Op.EQ, tunnelUuid);
        List<TunnelInterfaceVO> tunnelInterfaces = qTunnelInterface.list();

        return tunnelInterfaces;
    }

    /**
     * 随机指定范围内N个不重复的数
     *
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n   随机数个数
     */
    public static int[] randomCommon(int min, int max, int n) {
/*        if (n > (max - min + 1) || max < min) {
            // throw new IllegalArgumentException("random num count must be greater than the difference between the maximum and the minimum ！");

        }*/
        if (min > max) {
            throw new IllegalArgumentException("max must grater than or equal to min！");
        }

        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    /**
     * 随机产生指定范围内1个数
     *
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     */
    public int randonCommon(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("max must grater than or equal to min！");
        }
        int num = (int) (Math.random() * (max - min)) + min;
        return num;
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
        } else if (msg instanceof APIUpdateTunnelMonitorMsg) {
            validate((APIUpdateTunnelMonitorMsg) msg);
        } else if (msg instanceof APICreateMonitorCidrMsg) {
            validate((APICreateMonitorCidrMsg) msg);
        } else if (msg instanceof APICreateHostSwitchMonitorMsg) {
            validate((APICreateHostSwitchMonitorMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateHostSwitchMonitorMsg msg) {
        //判断监控机和物理交换机所属节点是否一样
        HostVO hostVO = dbf.findByUuid(msg.getHostUuid(), HostVO.class);
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

    private void validate(APIUpdateTunnelMonitorMsg msg) {
        //验证monitorIp合法性
        if (!NetworkUtils.isIpv4Address(msg.getMonitorAIp()))
            throw new ApiMessageInterceptionException(argerr("Illegal monitor IP %s！", msg.getMonitorAIp()));

        if (!NetworkUtils.isIpv4Address(msg.getMonitorZIp()))
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
