package org.zstack.tunnel.manage;

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
import org.zstack.core.db.GLock;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.tunnel.header.node.NodeVO;
import org.zstack.tunnel.header.switchs.*;
import org.zstack.tunnel.header.tunnel.*;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.TypedQuery;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.zstack.core.Platform.argerr;

/**
 * Created by DCY on 2017-08-21
 */
public class TunnelManagerImpl  extends AbstractService implements TunnelManager,ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(TunnelManagerImpl.class);

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
        if(msg instanceof APICreateNetworkMsg){
            handle((APICreateNetworkMsg) msg);
        }else if(msg instanceof APICreateNetworkManualMsg){
            handle((APICreateNetworkManualMsg) msg);
        }else if(msg instanceof APIUpdateNetworkMsg){
            handle((APIUpdateNetworkMsg) msg);
        }else if(msg instanceof APIDeleteNetworkMsg){
            handle((APIDeleteNetworkMsg) msg);
        }else if(msg instanceof APICreateInterfaceMsg){
            handle((APICreateInterfaceMsg) msg);
        }else if(msg instanceof APICreateInterfaceManualMsg){
            handle((APICreateInterfaceManualMsg) msg);
        }else if(msg instanceof APIUpdateInterfaceMsg){
            handle((APIUpdateInterfaceMsg) msg);
        }else if(msg instanceof APIDeleteInterfaceMsg){
            handle((APIDeleteInterfaceMsg) msg);
        }else if(msg instanceof APICreateTunnelMsg){
            handle((APICreateTunnelMsg) msg);
        }else if(msg instanceof APICreateTunnelManualMsg){
            handle((APICreateTunnelManualMsg) msg);
        }else if(msg instanceof APIUpdateTunnelMsg){
            handle((APIUpdateTunnelMsg) msg);
        }else if(msg instanceof APIDeleteTunnelMsg){
            handle((APIDeleteTunnelMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateNetworkMsg msg){
        NetworkVO vo = new NetworkVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());

        GLock glock = new GLock("maxvsi", 120);
        glock.lock();
        String sql = "select max(vo.vsi) from NetworkVO vo";
        try {
            TypedQuery<Integer> vq = dbf.getEntityManager().createQuery(sql, Integer.class);
            Integer vsi = vq.getSingleResult();

            if(vsi == null){
                vo.setVsi(1);
            }else{
                vo.setVsi(vsi+1);
            }

            vo.setName(msg.getName());
            vo.setMonitorCidr(msg.getMonitorCidr());
            vo.setDescription(msg.getDescription());

            vo = dbf.persistAndRefresh(vo);

            APICreateNetworkEvent evt = new APICreateNetworkEvent(msg.getId());
            evt.setInventory(NetworkInventory.valueOf(vo));
            bus.publish(evt);

        }finally {
            glock.unlock();
        }
    }

    private void handle(APICreateNetworkManualMsg msg){
        NetworkVO vo = new NetworkVO();

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setVsi(msg.getVsi());
        vo.setName(msg.getName());
        vo.setMonitorCidr(msg.getMonitorCidr());
        vo.setDescription(msg.getDescription());

        vo = dbf.persistAndRefresh(vo);

        APICreateNetworkManualEvent evt = new APICreateNetworkManualEvent(msg.getId());
        evt.setInventory(NetworkInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateNetworkMsg msg){
        NetworkVO vo = dbf.findByUuid(msg.getUuid(),NetworkVO.class);
        boolean update = false;
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateNetworkEvent evt = new APIUpdateNetworkEvent(msg.getId());
        evt.setInventory(NetworkInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteNetworkMsg msg){
        String uuid = msg.getUuid();
        NetworkVO vo = dbf.findByUuid(uuid,NetworkVO.class);

        if (vo != null) {
            dbf.remove(vo);
        }

        APIDeleteNetworkEvent evt = new APIDeleteNetworkEvent(msg.getId());
        evt.setInventory(NetworkInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateInterfaceMsg msg){
        InterfaceVO vo = new InterfaceVO();

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());

        //策略分配端口
        TunnelStrategy ts = new TunnelStrategy();
        String switchPortUuid = ts.getSwitchPortByStrategy(msg.getEndpointUuid() ,msg.getPortAttribute() ,msg.getPortType());
        if(switchPortUuid == null){
            throw new ApiMessageInterceptionException(argerr("该连接点下无可用的云共享端口"));
        }

        vo.setSwitchPortUuid(switchPortUuid);
        vo.setBandwidth(msg.getBandwidth());
        vo.setMonths(msg.getMonths());
        vo.setExpiredDate(Timestamp.valueOf(LocalDateTime.now().plus(msg.getMonths(), ChronoUnit.MONTHS)));
        vo.setDescription(msg.getDescription());

        vo = dbf.persistAndRefresh(vo);

        APICreateInterfaceEvent evt = new APICreateInterfaceEvent(msg.getId());
        evt.setInventory(InterfaceInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateInterfaceManualMsg msg){
        InterfaceVO vo = new InterfaceVO();

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setSwitchPortUuid(msg.getSwitchPortUuid());
        vo.setBandwidth(msg.getBandwidth());
        vo.setMonths(msg.getMonths());
        vo.setExpiredDate(Timestamp.valueOf(LocalDateTime.now().plus(msg.getMonths(), ChronoUnit.MONTHS)));
        vo.setDescription(msg.getDescription());

        vo = dbf.persistAndRefresh(vo);

        APICreateInterfaceManualEvent evt = new APICreateInterfaceManualEvent(msg.getId());
        evt.setInventory(InterfaceInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateInterfaceMsg msg){
        InterfaceVO vo = dbf.findByUuid(msg.getUuid(),InterfaceVO.class);
        boolean update = false;
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
            update = true;
        }
        if(msg.getBandwidth() != null){
            vo.setBandwidth(msg.getBandwidth());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateInterfaceEvent evt = new APIUpdateInterfaceEvent(msg.getId());
        evt.setInventory(InterfaceInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteInterfaceMsg msg){
        InterfaceEO eo = dbf.findByUuid(msg.getUuid(),InterfaceEO.class);
        InterfaceVO vo = dbf.findByUuid(msg.getUuid(),InterfaceVO.class);

        eo.setDeleted(1);

        eo = dbf.updateAndRefresh(eo);

        APIDeleteInterfaceEvent evt = new APIDeleteInterfaceEvent(msg.getId());
        evt.setInventory(InterfaceInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APICreateTunnelMsg msg){
        TunnelVO vo = new TunnelVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setNetworkUuid(msg.getNetworkUuid());
        vo.setName(msg.getName());

        TunnelStrategy ts = new TunnelStrategy();

        //给A端口分配外部vlan,并创建TunnelInterface
        Integer vlanA = ts.getInnerVlanByStrategy(msg.getNetworkUuid() ,msg.getInterfaceAUuid());
        if(vlanA == 0){
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }
        TunnelInterfaceVO tivoA = new TunnelInterfaceVO();
        tivoA.setUuid(Platform.getUuid());
        tivoA.setTunnelUuid(vo.getUuid());
        tivoA.setInterfaceUuid(msg.getInterfaceAUuid());
        tivoA.setVlan(vlanA);
        tivoA.setSortTag("A");
        tivoA.setQinqState(msg.getQinqStateA());
        dbf.getEntityManager().persist(tivoA);
        tivoA.setInterfaceVO(dbf.findByUuid(msg.getInterfaceAUuid(),InterfaceVO.class));

        //给Z端口分配外部vlan,并创建TunnelInterface
        Integer vlanZ = ts.getInnerVlanByStrategy(msg.getNetworkUuid() ,msg.getInterfaceZUuid());
        if(vlanZ == 0){
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
        }
        TunnelInterfaceVO tivoZ = new TunnelInterfaceVO();
        tivoZ.setUuid(Platform.getUuid());
        tivoZ.setTunnelUuid(vo.getUuid());
        tivoZ.setInterfaceUuid(msg.getInterfaceZUuid());
        tivoZ.setVlan(vlanZ);
        tivoZ.setSortTag("Z");
        tivoZ.setQinqState(msg.getQinqStateZ());
        dbf.getEntityManager().persist(tivoZ);
        tivoZ.setInterfaceVO(dbf.findByUuid(msg.getInterfaceZUuid(),InterfaceVO.class));

        //如果开启Qinq,需要指定内部vlan段
        //在同一个专有网络下，该端口没有被使用过，才会让用户选择是否开启Qinq,否则是否开启属性和同一个端口一致
        if(msg.getQinqStateA() == TunnelQinqState.Enabled || msg.getQinqStateZ() == TunnelQinqState.Enabled){
            List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();
            for(InnerVlanSegment vlanSegment:vlanSegments){
                QinqVO qvo = new QinqVO();
                qvo.setUuid(Platform.getUuid());
                qvo.setTunnelUuid(vo.getUuid());
                qvo.setStartVlan(vlanSegment.getStartVlan());
                qvo.setEndVlan(vlanSegment.getEndVlan());
                dbf.getEntityManager().persist(qvo);
            }
        }

        //根据经纬度算距离
        NodeVO nvoA = dbf.findByUuid(msg.getNodeAUuid(),NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(msg.getNodeZUuid(),NodeVO.class);
        vo.setDistance(Distance.getDistance(nvoA.getLongtitude(),nvoA.getLatitude(),nvoZ.getLongtitude(),nvoZ.getLatitude()));

        vo.setMonths(msg.getMonths());
        vo.setBandwidth(msg.getBandwidth());
        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setExpiredDate(dbf.getCurrentSqlTime());
        vo.setDescription(msg.getDescription());
        dbf.getEntityManager().persist(vo);

        vo.setNetworkVO(dbf.findByUuid(msg.getNetworkUuid(),NetworkVO.class));
        List<TunnelInterfaceVO> tivo = new ArrayList<TunnelInterfaceVO>();
        tivo.add(tivoA);
        tivo.add(tivoZ);
        vo.setTunnelInterfaceVO(tivo);

        //默认给创建的通道开启监控
        MonitorManagerImpl monitorManager = new MonitorManagerImpl();
        //monitorManager.handleMessage();

        APICreateTunnelEvent evt = new APICreateTunnelEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APICreateTunnelManualMsg msg){
        TunnelVO vo = new TunnelVO();
        vo.setUuid(Platform.getUuid());

        vo.setAccountUuid(msg.getAccountUuid());
        vo.setNetworkUuid(msg.getNetworkUuid());
        vo.setName(msg.getName());

        TunnelInterfaceVO tivoA = new TunnelInterfaceVO();
        tivoA.setUuid(Platform.getUuid());
        tivoA.setTunnelUuid(vo.getUuid());
        tivoA.setInterfaceUuid(msg.getInterfaceAUuid());
        tivoA.setVlan(msg.getaVlan());
        tivoA.setSortTag("A");
        tivoA.setQinqState(msg.getQinqStateA());
        dbf.getEntityManager().persist(tivoA);
        tivoA.setInterfaceVO(dbf.findByUuid(msg.getInterfaceAUuid(),InterfaceVO.class));

        TunnelInterfaceVO tivoZ = new TunnelInterfaceVO();
        tivoZ.setUuid(Platform.getUuid());
        tivoZ.setTunnelUuid(vo.getUuid());
        tivoZ.setInterfaceUuid(msg.getInterfaceZUuid());
        tivoZ.setVlan(msg.getzVlan());
        tivoZ.setSortTag("Z");
        tivoZ.setQinqState(msg.getQinqStateZ());
        dbf.getEntityManager().persist(tivoZ);
        tivoZ.setInterfaceVO(dbf.findByUuid(msg.getInterfaceZUuid(),InterfaceVO.class));

        //如果开启Qinq,需要指定内部vlan段
        //在同一个专有网络下，该端口没有被使用过，才会让用户选择是否开启Qinq,否则是否开启属性和同一个端口一致
        if(msg.getQinqStateA() == TunnelQinqState.Enabled || msg.getQinqStateZ() == TunnelQinqState.Enabled){
            List<InnerVlanSegment> vlanSegments = msg.getVlanSegment();

            for(InnerVlanSegment vlanSegment:vlanSegments){
                QinqVO qvo = new QinqVO();
                qvo.setUuid(Platform.getUuid());
                qvo.setTunnelUuid(vo.getUuid());
                qvo.setStartVlan(vlanSegment.getStartVlan());
                qvo.setEndVlan(vlanSegment.getEndVlan());
                dbf.getEntityManager().persist(qvo);
            }
        }

        vo.setMonths(msg.getMonths());
        vo.setBandwidth(msg.getBandwidth());

        //根据经纬度算距离
        NodeVO nvoA = dbf.findByUuid(msg.getNodeAUuid(),NodeVO.class);
        NodeVO nvoZ = dbf.findByUuid(msg.getNodeZUuid(),NodeVO.class);
        vo.setDistance(Distance.getDistance(nvoA.getLongtitude(),nvoA.getLatitude(),nvoZ.getLongtitude(),nvoZ.getLatitude()));

        vo.setState(TunnelState.Unpaid);
        vo.setStatus(TunnelStatus.Disconnected);
        vo.setMonitorState(TunnelMonitorState.Disabled);
        vo.setExpiredDate(dbf.getCurrentSqlTime());
        vo.setDescription(msg.getDescription());

        dbf.getEntityManager().persist(vo);

        vo.setNetworkVO(dbf.findByUuid(msg.getNetworkUuid(),NetworkVO.class));
        List<TunnelInterfaceVO> tivo = new ArrayList<TunnelInterfaceVO>();
        tivo.add(tivoA);
        tivo.add(tivoZ);
        vo.setTunnelInterfaceVO(tivo);

        APICreateTunnelManualEvent evt = new APICreateTunnelManualEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateTunnelMsg msg){
        TunnelVO vo = dbf.findByUuid(msg.getUuid(),TunnelVO.class);
        boolean update = false;

        if(msg.getNetworkUuid() != null){
            vo.setNetworkUuid(msg.getNetworkUuid());
            update = true;
        }
        if(msg.getBandwidth() != null){
            vo.setBandwidth(msg.getBandwidth());
            update = true;
        }
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getDistance() != null){
            vo.setDistance(msg.getDistance());
            update = true;
        }
        if(msg.getState() != null){
            vo.setState(msg.getState());
            update = true;
        }
        if(msg.getStatus() != null){
            vo.setStatus(msg.getStatus());
            update = true;
        }
        if(msg.getMonitorState() != null){
            vo.setMonitorState(msg.getMonitorState());
            update = true;
        }
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
            update = true;
        }
        if(msg.getMonths() != null){
            vo.setMonths(msg.getMonths());
            update = true;
        }
        if(msg.getExpiredDate() != null){
            vo.setExpiredDate(msg.getExpiredDate());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateTunnelEvent evt = new APIUpdateTunnelEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APIDeleteTunnelMsg msg){
        TunnelEO eo = dbf.findByUuid(msg.getUuid(),TunnelEO.class);
        TunnelVO vo = dbf.findByUuid(msg.getUuid(),TunnelVO.class);

        eo.setDeleted(1);

        eo = dbf.getEntityManager().merge(eo);

        String tunnelUuid = msg.getUuid();

        //删除对应的 TunnelInterfaceVO 和 QingqVO
        SimpleQuery<TunnelInterfaceVO> q = dbf.createQuery(TunnelInterfaceVO.class);
        q.add(TunnelInterfaceVO_.tunnelUuid, SimpleQuery.Op.EQ, tunnelUuid);
        List<TunnelInterfaceVO> tivList = q.list();
        if (tivList.size() > 0) {
            for(TunnelInterfaceVO tiv : tivList){
                dbf.getEntityManager().remove(tiv);
            }
        }
        SimpleQuery<QinqVO> q2 = dbf.createQuery(QinqVO.class);
        q2.add(QinqVO_.tunnelUuid, SimpleQuery.Op.EQ, tunnelUuid);
        List<QinqVO> qinqList = q2.list();
        if (qinqList.size() > 0) {
            for(QinqVO qv : qinqList){
                dbf.getEntityManager().remove(qv);
            }
        }
        APIDeleteTunnelEvent evt = new APIDeleteTunnelEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
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
        return bus.makeLocalServiceId(TunnelConstant.SERVICE_ID);
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if(msg instanceof APICreateNetworkMsg){
            validate((APICreateNetworkMsg) msg);
        }else if(msg instanceof APICreateNetworkManualMsg){
            validate((APICreateNetworkManualMsg) msg);
        }else if(msg instanceof APIUpdateNetworkMsg){
            validate((APIUpdateNetworkMsg) msg);
        }else if(msg instanceof APIDeleteNetworkMsg){
            validate((APIDeleteNetworkMsg) msg);
        }else if(msg instanceof APICreateInterfaceMsg){
            validate((APICreateInterfaceMsg) msg);
        }else if(msg instanceof APICreateInterfaceManualMsg){
            validate((APICreateInterfaceManualMsg) msg);
        }else if(msg instanceof APIUpdateInterfaceMsg){
            validate((APIUpdateInterfaceMsg) msg);
        }else if(msg instanceof APIDeleteInterfaceMsg){
            validate((APIDeleteInterfaceMsg) msg);
        }else if(msg instanceof APICreateTunnelMsg){
            validate((APICreateTunnelMsg) msg);
        }else if(msg instanceof APICreateTunnelManualMsg){
            validate((APICreateTunnelManualMsg) msg);
        }else if(msg instanceof APIUpdateTunnelMsg){
            validate((APIUpdateTunnelMsg) msg);
        }else if(msg instanceof APIDeleteTunnelMsg){
            validate((APIDeleteTunnelMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateNetworkMsg msg){
        //判断同一个用户的网络名称是否已经存在
        SimpleQuery<NetworkVO> q = dbf.createQuery(NetworkVO.class);
        q.add(NetworkVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(NetworkVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("network's name %s is already exist ",msg.getName()));
        }
    }

    private void validate(APICreateNetworkManualMsg msg){
        //判断同一个用户的网络名称是否已经存在
        SimpleQuery<NetworkVO> q = dbf.createQuery(NetworkVO.class);
        q.add(NetworkVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(NetworkVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("network's name %s is already exist ",msg.getName()));
        }
        //判断VSI是否已经存在
        SimpleQuery<NetworkVO> q2 = dbf.createQuery(NetworkVO.class);
        q2.add(NetworkVO_.vsi, SimpleQuery.Op.EQ, msg.getVsi());
        if(q2.isExists()){
            throw new ApiMessageInterceptionException(argerr("network's vsi %s is already exist ",msg.getVsi()));
        }
    }

    private void validate(APIUpdateNetworkMsg msg){
        if(msg.getName() != null){
            //判断同一个用户的网络名称是否已经存在
            SimpleQuery<NetworkVO> q = dbf.createQuery(NetworkVO.class);
            q.add(NetworkVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q.add(NetworkVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            q.add(NetworkVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q.isExists()){
                throw new ApiMessageInterceptionException(argerr("network's name %s is already exist ",msg.getName()));
            }
        }

    }

    private void validate(APIDeleteNetworkMsg msg){
        //判断该网络是否被专线使用
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.networkUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,network is being used!"));
        }

    }

    private void validate(APICreateInterfaceMsg msg){
        //判断同一个用户的接口名称是否已经存在
        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(InterfaceVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("Interface's name %s is already exist ",msg.getName()));
        }
    }

    private void validate(APICreateInterfaceManualMsg msg){
        //判断同一个用户的接口名称是否已经存在
        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(InterfaceVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("Interface's name %s is already exist ",msg.getName()));
        }
    }

    private void validate(APIUpdateInterfaceMsg msg){
        //判断同一个用户的网络名称是否已经存在
        if(msg.getName() != null){
            SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
            q.add(InterfaceVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q.add(InterfaceVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            q.add(InterfaceVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q.isExists()){
                throw new ApiMessageInterceptionException(argerr("Interface's name %s is already exist ",msg.getName()));
            }
        }

    }

    private void validate(APIDeleteInterfaceMsg msg){
        //判断云专线下是否有该物理接口
        SimpleQuery<TunnelInterfaceVO> q = dbf.createQuery(TunnelInterfaceVO.class);
        q.add(TunnelInterfaceVO_.interfaceUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("cannot delete,interface is being used!"));
        }
    }

    private void validate(APICreateTunnelMsg msg){
        //判断同一个用户和同一个专有网络下的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        q.add(TunnelVO_.networkUuid, SimpleQuery.Op.EQ, msg.getNetworkUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ",msg.getName()));
        }
        //判断通道两端的连接点是否相同，不允许相同
        if(msg.getEndpointPointAUuid() == msg.getEndpointPointZUuid()){
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }

        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select b.tunnelUuid from TunnelInterfaceVO b where b.interfaceUuid = :interfaceUuid and b.qinqState = 'Enabled') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        if(msg.getVlanSegment() != null){
            if(msg.getQinqStateA() == TunnelQinqState.Enabled){
                List<InnerVlanSegment> vlanSegments= msg.getVlanSegment();
                for(InnerVlanSegment vlanSegment:vlanSegments){
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("interfaceUuid",msg.getInterfaceAUuid());
                    vq.setParameter("startVlan",vlanSegment.getStartVlan());
                    vq.setParameter("endVlan",vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if(count > 0){
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
            if(msg.getQinqStateZ() == TunnelQinqState.Enabled){
                List<InnerVlanSegment> vlanSegments= msg.getVlanSegment();
                for(InnerVlanSegment vlanSegment:vlanSegments){
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("interfaceUuid",msg.getInterfaceZUuid());
                    vq.setParameter("startVlan",vlanSegment.getStartVlan());
                    vq.setParameter("endVlan",vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if(count > 0){
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
        }


    }

    private void validate(APICreateTunnelManualMsg msg) {

        //判断同一个用户和同一个专有网络下的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        q.add(TunnelVO_.networkUuid, SimpleQuery.Op.EQ, msg.getNetworkUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ", msg.getName()));
        }

        //判断通道两端的连接点是否相同，不允许相同
        if (msg.getEndpointPointAUuid() == msg.getEndpointPointZUuid()) {
            throw new ApiMessageInterceptionException(argerr("通道两端不允许在同一个连接点 "));
        }

        TunnelStrategy ts = new TunnelStrategy();
        //查询该TUNNEL的物理接口所属的虚拟交换机
        String switchUuidA = ts.findSwitchByInterface(msg.getInterfaceAUuid());
        String switchUuidZ = ts.findSwitchByInterface(msg.getInterfaceZUuid());

        //查询该虚拟交换机下所有的Vlan段
        List<SwitchVlanVO> vlanListA = ts.findSwitchVlanBySwitch(switchUuidA);
        List<SwitchVlanVO> vlanListZ = ts.findSwitchVlanBySwitch(switchUuidZ);

        //查询该虚拟交换机下已经分配的Vlan
        List<Integer> allocatedVlansA = ts.fingAllocateVlanBySwitch(switchUuidA);
        List<Integer> allocatedVlansZ = ts.fingAllocateVlanBySwitch(switchUuidZ);

        //判断外部VLAN是否在该虚拟交换机的VLAN段中
        Boolean innerA = false;
        for (SwitchVlanVO switchVlanVO : vlanListA) {
            if (msg.getaVlan() >= switchVlanVO.getStartVlan() && msg.getaVlan() <= switchVlanVO.getEndVlan()) {
                innerA = true;
                break;
            }
        }
        if (innerA == false) {
            throw new ApiMessageInterceptionException(argerr("avlan not in switchVlan"));
        }
        Boolean innerZ = false;
        for (SwitchVlanVO switchVlanVO : vlanListZ) {
            if (msg.getzVlan() >= switchVlanVO.getStartVlan() && msg.getzVlan() <= switchVlanVO.getEndVlan()) {
                innerZ = true;
                break;
            }
        }
        if (innerZ == false) {
            throw new ApiMessageInterceptionException(argerr("zvlan not in switchVlan"));
        }

        //判断外部vlan是否可用
        //给A端口vlan验证
        //同一个VSI下同一个物理接口不用分配vlan，他们vlan一样
        Integer aVlan = null;
        aVlan = ts.findVlanForSameVsiAndInterface(msg.getNetworkUuid(), msg.getInterfaceAUuid());
        if (aVlan != -1) {
            if (aVlan != msg.getaVlan()) {
                throw new ApiMessageInterceptionException(argerr("该端口在同一专有网络下已经分配vlan，请使用该vlan %s ", aVlan));
            }
        } else {
            if (allocatedVlansA.contains(msg.getaVlan())) {
                throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", msg.getaVlan()));
            }
        }
        //给Z端口vlan验证
        //同一个VSI下同一个物理接口不用分配vlan，他们vlan一样
        Integer zVlan = null;
        zVlan = ts.findVlanForSameVsiAndInterface(msg.getNetworkUuid(), msg.getInterfaceZUuid());
        if (zVlan != -1) {
            if (zVlan != msg.getzVlan()) {
                throw new ApiMessageInterceptionException(argerr("该端口在同一专有网络下已经分配vlan，请使用该vlan %s ", zVlan));
            }
        } else {
            if (allocatedVlansZ.contains(msg.getzVlan())) {
                throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用", msg.getzVlan()));
            }


        }

        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.tunnelUuid in (select tunnelUuid from TunnelInterfaceVO where interfaceUuid = :interfaceUuid and qinqState = 'Enabled') " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        if(msg.getVlanSegment() != null){
            if(msg.getQinqStateA() == TunnelQinqState.Enabled){
                List<InnerVlanSegment> vlanSegments= msg.getVlanSegment();
                for(InnerVlanSegment vlanSegment:vlanSegments){
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("interfaceUuid",msg.getInterfaceAUuid());
                    vq.setParameter("startVlan",vlanSegment.getStartVlan());
                    vq.setParameter("endVlan",vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if(count > 0){
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
            if(msg.getQinqStateZ() == TunnelQinqState.Enabled){
                List<InnerVlanSegment> vlanSegments= msg.getVlanSegment();
                for(InnerVlanSegment vlanSegment:vlanSegments){
                    TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                    vq.setParameter("interfaceUuid",msg.getInterfaceZUuid());
                    vq.setParameter("startVlan",vlanSegment.getStartVlan());
                    vq.setParameter("endVlan",vlanSegment.getEndVlan());
                    Long count = vq.getSingleResult();
                    if(count > 0){
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
        }
    }

    private void validate(APIUpdateTunnelMsg msg){
        //判断同一个用户和同一个专有网络下的名称是否已经存在
        if(msg.getName() != null){
            SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
            q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            q.add(TunnelVO_.networkUuid, SimpleQuery.Op.EQ, msg.getNetworkUuid());
            q.add(TunnelVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q.isExists()){
                throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ",msg.getName()));
            }
        }

    }

    private void validate(APIDeleteTunnelMsg msg){ }
}
