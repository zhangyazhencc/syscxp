package com.syscxp.tunnel.manage;

import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.tunnel.header.host.*;
import com.syscxp.tunnel.header.monitor.*;
import com.syscxp.tunnel.header.switchs.PhysicalSwitchAccessType;
import com.syscxp.tunnel.header.switchs.PhysicalSwitchVO;
import com.syscxp.tunnel.header.tunnel.TunnelInterfaceVO_;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.apache.commons.lang.StringUtils;
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
import com.syscxp.tunnel.header.tunnel.TunnelInterfaceVO;
import com.syscxp.tunnel.header.tunnel.TunnelVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
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
         if (msg instanceof APICreateHostSwitchMonitorMsg) {
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

    private void handle(APICreateTunnelMonitorMsg msg) {
        TunnelMonitorVO tunnelMonitorVO = createTunnelMonitorHandle(msg);

        // 下发监控通道配置
        ControllerCommands.TunnelMonitorCommand cmd = getTunnelMonitorCommand(tunnelMonitorVO.getTunnelUuid());
        String command = JSONObjectUtil.toJsonString(cmd);

        APICreateTunnelMonitorEvent event = new APICreateTunnelMonitorEvent(msg.getId());
        ControllerRestFacade crf = new ControllerRestFacade();
        crf.sendCommand(ControllerRestConstant.SYNC_TEST, command, new Completion(event) {
            @Override
            public void success() {
                logger.info("执行成功！");
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("执行失败！");
            }
        });



        event.setInventory(TunnelMonitorInventory.valueOf(tunnelMonitorVO));
        bus.publish(event);
    }

    private void handle(APIUpdateTunnelMonitorMsg msg) {
        TunnelMonitorVO tunnelMonitorVO = dbf.findByUuid(msg.getUuid(), TunnelMonitorVO.class);

        tunnelMonitorVO.setTunnelUuid(msg.getTunnelUuid());
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
        tunnelMonitorVO.setAccountUuid(msg.getSession().getAccountUuid());
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
        TunnelVO tunnel;
        MonitorCidrVO monitorCidr;

        // A/Z至少有一个不存在
        if (!monitorIps.containsKey(InterfaceType.A.toString()) || !monitorIps.containsKey(InterfaceType.Z.toString())) {
            tunnel = dbf.findByUuid(tunnelUuid, TunnelVO.class);
            if (tunnel != null) {
                SimpleQuery<MonitorCidrVO> qMonitorCidr = dbf.createQuery(MonitorCidrVO.class);
                qMonitorCidr.add(MonitorCidrVO_.monitorCidr, SimpleQuery.Op.EQ, tunnel.getMonitorCidr());
                monitorCidr = qMonitorCidr.find();

                if (monitorCidr != null) {
                    //TODO: 性能优化
                    // vsi下已有监控ip
                    String sqlExistedMonitorIp = "select c.monitorIp " +
                            "from TunnelVO a,TunnelMonitorVO b,TunnelMonitorInterfaceVO c " +
                            "where a.uuid = :tunnelUuid " +
                            "and b.tunnelUuid = a.uuid " +
                            "and c.tunnelMonitorUuid = b.uuid";
                    TypedQuery<String> qExistedMonitorIp = dbf.getEntityManager().createQuery(sqlExistedMonitorIp, String.class);
                    qExistedMonitorIp.setParameter("tunnelUuid", tunnelUuid);
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
                        if (hostUuidlist.size() <= 0) {
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
        return (int) (Math.random() * (max - min)) + min;
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

    private void validate(APICreateMonitorCidrMsg msg) {
        //判断监控网段是否已经存在
        SimpleQuery<MonitorCidrVO> q = dbf.createQuery(MonitorCidrVO.class);
        q.add(MonitorCidrVO_.monitorCidr, SimpleQuery.Op.EQ, msg.getMonitorCidr());
        if (q.isExists())
            throw new ApiMessageInterceptionException(argerr("MonitorCird %s is already exist ", msg.getMonitorCidr()));

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

    private void validate(APIUpdateTunnelMonitorMsg msg) {
        //验证monitorIp合法性
        if (!NetworkUtils.isIpv4Address(msg.getMonitorAIp()))
            throw new ApiMessageInterceptionException(argerr("Illegal monitor IP %s！", msg.getMonitorAIp()));

        if (!NetworkUtils.isIpv4Address(msg.getMonitorZIp()))
            throw new ApiMessageInterceptionException(argerr("Illegal monitor IP %s！", msg.getMonitorZIp()));
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

    /**
     * 获取监控下发controller命令
     *
     * @param tunnelUuid
     * @return
     */
    public ControllerCommands.TunnelMonitorCommand getTunnelMonitorCommand(String tunnelUuid) {
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
            monitorIp.put(monitor.get(0).toString(), monitor.get(1, String.class).toString());
            monitorPort.put(monitor.get(0).toString(), monitor.get(2, String.class).toString());
        }
    }
}
