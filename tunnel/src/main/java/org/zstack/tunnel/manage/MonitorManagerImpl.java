package org.zstack.tunnel.manage;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.cloudbus.ResourceDestinationMaker;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.db.Q;
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
import org.zstack.tunnel.header.switchs.SwitchPortState;
import org.zstack.tunnel.header.switchs.SwitchState;
import org.zstack.tunnel.header.switchs.SwitchStatus;
import org.zstack.tunnel.header.switchs.SwitchVO;
import org.zstack.tunnel.header.tunnel.NetworkVO;
import org.zstack.tunnel.header.tunnel.TunnelInterfaceVO;
import org.zstack.tunnel.header.tunnel.TunnelInterfaceVO_;
import org.zstack.tunnel.header.tunnel.TunnelVO;
import org.zstack.tunnel.sdk.sdn.service.RyuController;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;
import org.zstack.utils.network.NetworkUtils;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    //@Autowired
    //private RyuController ryuController;


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

    @Transactional
    private void handle(APICreateTunnelMonitorMsg msg) {

        TunnelMonitorVO tunnelMonitorVO = createTunnelMonitorHandle(msg);

        // 下发监控通道配置
        // result = ryuController.tunnelMonitorIssue(tunnelMonitorVO.getTunnelUuid(),tunnelMonitorVO.getUuid());
        // 成功 返回
        // 失败 报错
        //
        //

/*        List<TunnelMonitorInterfaceVO> tunnelMonitorInterfaceVOS = new ArrayList<TunnelMonitorInterfaceVO>();
        tunnelMonitorInterfaceVOS.add(interfaceA);
        tunnelMonitorInterfaceVOS.add(interfaceZ);
        tunnelMonitorVO.setTunnelMonitorInterfaceVOList(tunnelMonitorInterfaceVOS);*/

        TunnelMonitorVO resultVo = dbf.findByUuid(tunnelMonitorVO.getUuid(),TunnelMonitorVO.class);

        APICreateTunnelMonitorEvent event = new APICreateTunnelMonitorEvent(msg.getId());
        event.setInventory(TunnelMonitorInventory.valueOf(resultVo));
        bus.publish(event);
    }

    public TunnelMonitorVO createTunnelMonitorHandle(APICreateTunnelMonitorMsg msg){
        // TunnelMonitorVO
        TunnelMonitorVO tunnelMonitorVO = new TunnelMonitorVO();
        tunnelMonitorVO.setUuid(Platform.getUuid());
        tunnelMonitorVO.setTunnelUuid(msg.getTunnelUuid());
        tunnelMonitorVO.setHostAUuid(msg.getHostAUuid());
        tunnelMonitorVO.setMonitorAIp(msg.getMonitorAIp());
        tunnelMonitorVO.setHostZUuid(msg.getHostZUuid());
        tunnelMonitorVO.setMonitorZIp(msg.getMonitorZIp());
        tunnelMonitorVO.setStatus(msg.getStatus());
        tunnelMonitorVO.setMsg(msg.getMsg());
        dbf.getEntityManager().persist(tunnelMonitorVO);

        // 监控IP按tunnel监控IP段随机产生
        Map<String,String> monitorIps = generateMonirotIps(tunnelMonitorVO.getTunnelUuid());

        //TODO: 按tunnel查询两端监控机
        // Map<String,String> monitorHosts = getMonirotHost(tunnelMonitorVO.getTunnelUuid());

        // TunnelMonitorInterfaceVO(A)
        TunnelMonitorInterfaceVO interfaceA = new TunnelMonitorInterfaceVO();
        interfaceA.setUuid(Platform.getUuid());
        interfaceA.setInterfaceType(InterfaceType.A);
        interfaceA.setTunnelMonitorUuid(tunnelMonitorVO.getUuid());
        interfaceA.setHostUuid(tunnelMonitorVO.getHostAUuid());
        interfaceA.setMonitorIp(monitorIps.get("A"));
        dbf.getEntityManager().persist(interfaceA);

        // TunnelMonitorInterfaceVO(Z)
        TunnelMonitorInterfaceVO interfaceZ = new TunnelMonitorInterfaceVO();
        interfaceZ.setUuid(Platform.getUuid());
        interfaceZ.setInterfaceType(InterfaceType.Z);
        interfaceZ.setTunnelMonitorUuid(tunnelMonitorVO.getUuid());
        interfaceZ.setHostUuid(tunnelMonitorVO.getHostZUuid());
        interfaceZ.setMonitorIp(monitorIps.get("Z"));
        dbf.getEntityManager().persist(interfaceZ);

        return tunnelMonitorVO;
    }

    @Transactional
    private void handle(APIUpdateTunnelMonitorMsg msg) {
        TunnelMonitorVO tunnelMonitorVO = dbf.findByUuid(msg.getUuid(), TunnelMonitorVO.class);

        tunnelMonitorVO.setTunnelUuid(msg.getTunnelUuid());
        tunnelMonitorVO.setHostAUuid(msg.getHostAUuid());
        tunnelMonitorVO.setMonitorAIp(msg.getMonitorAIp());
        tunnelMonitorVO.setHostZUuid(msg.getHostZUuid());
        tunnelMonitorVO.setMonitorZIp(msg.getMonitorZIp());
        tunnelMonitorVO.setStatus(msg.getStatus());
        if (msg.getMsg() != null) {
            tunnelMonitorVO.setMsg(msg.getMsg());
        }
        dbf.getEntityManager().merge(tunnelMonitorVO);

        List<TunnelMonitorInterfaceVO> interfaceVOList = tunnelMonitorVO.getTunnelMonitorInterfaceVOList(); //new TunnelMonitorInterfaceVO();

        TunnelMonitorInterfaceVO interfaceVO = new TunnelMonitorInterfaceVO();
        for(TunnelMonitorInterfaceVO vo : interfaceVOList){
            if(InterfaceType.A.equals(vo.getInterfaceType())){
                interfaceVO.setMonitorIp(msg.getMonitorAIp());
                interfaceVO.setHostUuid(msg.getHostAUuid());
                dbf.getEntityManager().merge(interfaceVO);
            }else if(InterfaceType.Z.equals(vo.getInterfaceType())){
                interfaceVO.setMonitorIp(msg.getMonitorZIp());
                interfaceVO.setHostUuid(msg.getHostZUuid());
                dbf.getEntityManager().merge(interfaceVO);
            }
        }

        TunnelMonitorVO resultVo = dbf.findByUuid(msg.getUuid(),TunnelMonitorVO.class);
        APIUpdateTunnelMonitorEvent evt = new APIUpdateTunnelMonitorEvent(msg.getId());
        evt.setInventory(TunnelMonitorInventory.valueOf(resultVo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APIDeleteTunnelMonitorMsg msg) {
        String uuid = msg.getUuid();
        TunnelMonitorVO tunnelMonitorVO = dbf.findByUuid(msg.getUuid(), TunnelMonitorVO.class);

        dbf.getEntityManager().remove(tunnelMonitorVO);
        for(TunnelMonitorInterfaceVO vo : tunnelMonitorVO.getTunnelMonitorInterfaceVOList()){
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
     * 按tunnel监控IP段随机生成监控IP
     * @param tunnelUuid
     * @return：A/Z两端监控IP
     */
    private Map<String,String> generateMonirotIps(String tunnelUuid){
        Map<String,String> monitorIps = new HashMap<String,String>(2);
        NetworkVO network;
        TunnelVO tunnel;
        MonitorCidrVO monitorCidr;

        tunnel = dbf.findByUuid(tunnelUuid,TunnelVO.class);
        if(tunnel != null){
            network = dbf.findByUuid(tunnel.getNetworkUuid(),NetworkVO.class);

            if(network!=null){
                SimpleQuery<MonitorCidrVO> qMonitorCidr = dbf.createQuery(MonitorCidrVO.class);
                qMonitorCidr.add(MonitorCidrVO_.monitorCidr,SimpleQuery.Op.EQ,network.getMonitorCidr());
                monitorCidr = qMonitorCidr.find();

                if(monitorCidr!=null){
                    String[] startIps=monitorCidr.getStartAddress().split(".");
                    String[] endIps=monitorCidr.getEndAddress().split(".");

                    // IP第三位
                    int[] ip2 = randomCommon(Integer.parseInt(startIps[2]),Integer.parseInt(endIps[2]),2);

                    startIps[2] = String.valueOf(ip2[0]);
                    endIps[2] = String.valueOf(ip2[1]);

                    // IP第四位
                    int[] ip3 = randomCommon(Integer.parseInt(startIps[3]),Integer.parseInt(endIps[3]),2);
                    startIps[3] = String.valueOf(ip3[0]);
                    endIps[3] = String.valueOf(ip3[1]);

                    monitorIps.put("A",StringUtils.join(startIps, "."));
                    monitorIps.put("Z",StringUtils.join(endIps, "."));
                }
            }
        }

        if(monitorIps==null){
            throw new IllegalArgumentException("monitor ip generator error!");
        }

        return monitorIps;
    }

    /**
     * 按tunnel查询两端监控主机
     * @param tunnelUuid
     * @return：A/Z两端监控主机
     */
    private Map<String,String> getMonirotHost(String tunnelUuid){
        Map<String,String> monitorHosts = new HashMap<String,String>(2);

        SimpleQuery<TunnelInterfaceVO> qTunnelInterface = dbf.createQuery(TunnelInterfaceVO.class);
        // qTunnelInterface.select(TunnelInterfaceVO_.tunnelUuid);
        qTunnelInterface.add(TunnelInterfaceVO_.tunnelUuid,SimpleQuery.Op.EQ,tunnelUuid);
        List<TunnelInterfaceVO> tunnelInterfaces = qTunnelInterface.list();

        for(TunnelInterfaceVO tunnelInterface : tunnelInterfaces){

            String sql = "";
            TypedQuery<String> vq = dbf.getEntityManager().createQuery(sql, String.class);
            //vq.setParameter("endpointUuid",);

            List<String> list = vq.getResultList();
        }
        return monitorHosts;
    }

    /**
     * 随机指定范围内N个不重复的数
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n 随机数个数
     */
    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            throw new IllegalArgumentException("random num count must be greater than the difference between the maximum and the minimum ！");
        }
        if(min >= max){
            throw new IllegalArgumentException("max must grater than min！");
        }

        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
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

        // TODO 检查tunnel对应的物理交换机两端是否有监控机
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
