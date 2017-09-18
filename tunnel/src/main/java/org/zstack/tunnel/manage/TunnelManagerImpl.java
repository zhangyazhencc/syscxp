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
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.tunnel.header.endpoint.EndpointType;
import org.zstack.tunnel.header.switchs.SwitchVlanVO;
import org.zstack.tunnel.header.tunnel.*;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;
import org.zstack.utils.network.NetworkUtils;

import javax.persistence.TypedQuery;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
        if(msg instanceof APICreateNetWorkMsg){
            handle((APICreateNetWorkMsg) msg);
        }else if(msg instanceof APICreateNetWorkManualMsg){
            handle((APICreateNetWorkManualMsg) msg);
        }else if(msg instanceof APIUpdateNetWorkMsg){
            handle((APIUpdateNetWorkMsg) msg);
        }else if(msg instanceof APIDeleteNetWorkMsg){
            handle((APIDeleteNetWorkMsg) msg);
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

    private void handle(APICreateNetWorkMsg msg){
        NetWorkVO vo = new NetWorkVO();

        vo.setUuid(Platform.getUuid());
        if(msg.getAccountUuid() == null){   //---nass
            vo.setAccountUuid(msg.getSession().getAccountUuid());
        }else{                              //---boss
            vo.setAccountUuid(msg.getAccountUuid());
        }
        String sql = "select max(vo.vsi) from NetWorkVO vo";
        TypedQuery<Integer> vq = dbf.getEntityManager().createQuery(sql, Integer.class);
        Integer vsi = vq.getSingleResult();
        if(vsi == null){
            vsi=0;
            vo.setVsi(vsi+1);
        }else{
            vo.setVsi(vsi+1);
        }
        vo.setName(msg.getName());
        vo.setMonitorCidr(msg.getMonitorCidr());
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }else{
            vo.setDescription(null);
        }

        vo = dbf.persistAndRefresh(vo);

        APICreateNetWorkEvent evt = new APICreateNetWorkEvent(msg.getId());
        evt.setInventory(NetWorkInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateNetWorkManualMsg msg){
        NetWorkVO vo = new NetWorkVO();

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setVsi(msg.getVsi());
        vo.setName(msg.getName());
        vo.setMonitorCidr(msg.getMonitorCidr());
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }else{
            vo.setDescription(null);
        }

        vo = dbf.persistAndRefresh(vo);

        APICreateNetWorkManualEvent evt = new APICreateNetWorkManualEvent(msg.getId());
        evt.setInventory(NetWorkInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateNetWorkMsg msg){
        NetWorkVO vo = dbf.findByUuid(msg.getUuid(),NetWorkVO.class);
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

        APIUpdateNetWorkEvent evt = new APIUpdateNetWorkEvent(msg.getId());
        evt.setInventory(NetWorkInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteNetWorkMsg msg){
        String uuid = msg.getUuid();
        NetWorkVO vo = dbf.findByUuid(uuid,NetWorkVO.class);

        if (vo != null) {
            dbf.remove(vo);
        }

        APIDeleteNetWorkEvent evt = new APIDeleteNetWorkEvent(msg.getId());
        evt.setInventory(NetWorkInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateInterfaceMsg msg){
        InterfaceVO vo = new InterfaceVO();

        vo.setUuid(Platform.getUuid());
        if(msg.getAccountUuid() == null){   //---nass
            vo.setAccountUuid(msg.getSession().getAccountUuid());
        }else{                              //---boss
            vo.setAccountUuid(msg.getAccountUuid());
        }

        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());
        EndpointType endpointType = msg.getEndpointType();
        vo.setIsExclusive(msg.getIsExclusive());

        String portUuid = null;

        if(endpointType.equals(EndpointType.CLOUD) && msg.getIsExclusive() ==0){  //云共享
            String sql = "select c.uuid from EndpointVO a,SwitchVO b,SwitchPortVO c " +
                    "where a.uuid = b.endpointUuid and b.uuid = c.switchUuid " +
                    "and a.uuid = :endpointUuid " +
                    "and b.enabled = 1 and b.status = 'NORMAL' " +
                    "and c.isExclusive = 0 and c.enabled = 1";
            TypedQuery<String> vq = dbf.getEntityManager().createQuery(sql, String.class);
            vq.setParameter("endpointUuid",msg.getEndpointUuid());
            List<String> portList = vq.getResultList();
            if(portList == null || portList.size() == 0){
                throw new ApiMessageInterceptionException(argerr("该连接点下无可用的云共享端口"));
            }else{
                Random r = new Random();
                portUuid = portList.get(r.nextInt(portList.size()));
            }
        }else if(endpointType.equals(EndpointType.CLOUD) && msg.getIsExclusive() ==1){ //云独享
            String sql = "select c.uuid from EndpointVO a, SwitchVO b,SwitchPortVO c " +
                    "where a.uuid = b.endpointUuid and b.uuid = c.switchUuid " +
                    "and a.uuid = :endpointUuid " +
                    "and b.enabled = 1 and b.status = 'NORMAL' " +
                    "and c.isExclusive = 1 and c.enabled = 1 and c.portType = :portType " +
                    "and c.uuid not in (select switchPortUuid from InterfaceVO)";
            TypedQuery<String> vq = dbf.getEntityManager().createQuery(sql, String.class);
            vq.setParameter("endpointUuid",msg.getEndpointUuid());
            vq.setParameter("portType",msg.getPortType().toString());
            List<String> portList = vq.getResultList();
            if(portList == null || portList.size() == 0){
                throw new ApiMessageInterceptionException(argerr("该连接点下无可用的云独享端口"));
            }else{
                Random r = new Random();
                portUuid = portList.get(r.nextInt(portList.size()));
            }

        }else if(endpointType.equals(EndpointType.ACCESSIN) && msg.getIsExclusive() ==0){ //接入共享
            String sql = "select c.uuid from EndpointVO a, SwitchVO b,SwitchPortVO c " +
                    "where a.uuid = b.endpointUuid and b.uuid = c.switchUuid " +
                    "and a.uuid = :endpointUuid " +
                    "and b.enabled = 1 and b.status = 'NORMAL' " +
                    "and c.isExclusive = 0 and c.enabled = 1 and c.portType = :portType ";
            TypedQuery<String> vq = dbf.getEntityManager().createQuery(sql, String.class);
            vq.setParameter("endpointUuid",msg.getEndpointUuid());
            vq.setParameter("portType",msg.getPortType().toString());
            List<String> portList = vq.getResultList();
            if(portList == null || portList.size() == 0){
                throw new ApiMessageInterceptionException(argerr("该连接点下无可用的接入共享端口"));
            }else{
                Random r = new Random();
                portUuid = portList.get(r.nextInt(portList.size()));
            }
        }else if(endpointType.equals(EndpointType.ACCESSIN) && msg.getIsExclusive() ==1){ //接入独享
            String sql = "select c.uuid from EndpointVO a,SwitchVO b,SwitchPortVO c " +
                    "where a.uuid = b.endpointUuid and b.uuid = c.switchUuid " +
                    "and a.uuid = :endpointUuid " +
                    "and b.enabled = 1 and b.status = 'NORMAL' " +
                    "and c.isExclusive = 1 and c.enabled = 1 and c.portType = :portType " +
                    "and c.uuid not in (select switchPortUuid from InterfaceVO)";
            TypedQuery<String> vq = dbf.getEntityManager().createQuery(sql, String.class);
            vq.setParameter("endpointUuid",msg.getEndpointUuid());
            vq.setParameter("portType",msg.getPortType().toString());
            List<String> portList = vq.getResultList();
            if(portList == null || portList.size() == 0){
                throw new ApiMessageInterceptionException(argerr("该连接点下无可用的接入独享端口"));
            }else{
                Random r = new Random();
                portUuid = portList.get(r.nextInt(portList.size()));
            }
        }
        vo.setSwitchPortUuid(portUuid);
        vo.setBandwidth(msg.getBandwidth());
        vo.setMonths(msg.getMonths());

        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTime(date);
        cal.add(Calendar.MONTH, msg.getMonths());
        date = cal.getTime();
        String time=format.format(date);
        vo.setExpiredDate(new Timestamp(date.getTime()).valueOf(time));

        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }else{
            vo.setDescription(null);
        }

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
        vo.setIsExclusive(msg.getIsExclusive());
        vo.setSwitchPortUuid(msg.getSwitchPortUuid());
        vo.setBandwidth(msg.getBandwidth());
        vo.setMonths(msg.getMonths());

        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTime(date);
        cal.add(Calendar.MONTH, msg.getMonths());
        date = cal.getTime();
        String time=format.format(date);
        vo.setExpiredDate(new Timestamp(date.getTime()).valueOf(time));

        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }else{
            vo.setDescription(null);
        }

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
        String tunnelUuid = Platform.getUuid();
        vo.setUuid(tunnelUuid);

        if(msg.getAccountUuid() == null){   //---nass
            vo.setAccountUuid(msg.getSession().getAccountUuid());
        }else{                              //---boss
            vo.setAccountUuid(msg.getAccountUuid());
        }

        vo.setNetWorkUuid(msg.getNetWorkUuid());
        vo.setName(msg.getName());
        vo.setInterfaceAUuid(msg.getInterfaceAUuid());
        vo.setInterfaceZUuid(msg.getInterfaceZUuid());

        //查询该TUNNEL的物理接口所属的虚拟交换机
        String switchUuidA = findSwitchByInterface(msg.getInterfaceAUuid());
        String switchUuidZ = findSwitchByInterface(msg.getInterfaceZUuid());

        //查询该虚拟交换机下所有的Vlan段
        List<SwitchVlanVO> vlanListA = findSwitchVlanBySwitch(switchUuidA);
        List<SwitchVlanVO> vlanListZ = findSwitchVlanBySwitch(switchUuidZ);

        //查询该虚拟交换机下已经分配的Vlan
        List<Integer> allocatedVlansA = fingAllocateVlanBySwitch(switchUuidA);
        List<Integer> allocatedVlansZ = fingAllocateVlanBySwitch(switchUuidZ);

        //给A端口分配外部vlan
            //同一个VSI下同一个物理接口不用分配vlan，他们vlan一样
        Integer aVlan = null;
        aVlan = findVlanForSameVsiAndInterface(msg.getNetWorkUuid(), msg.getInterfaceAUuid());
        if(aVlan != null){
            vo.setaVlan(aVlan);
        }else{
            aVlan = allocateVlan(vlanListA,allocatedVlansA);
            if(aVlan == 0){
                throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
            }
            vo.setaVlan(aVlan);
        }
        //给Z端口分配外部vlan
            //同一个VSI下同一个物理接口不用分配vlan，他们vlan一样
        Integer zVlan = null;
        zVlan = findVlanForSameVsiAndInterface(msg.getNetWorkUuid(), msg.getInterfaceZUuid());
        if(zVlan != null){
            vo.setzVlan(zVlan);
        }else{
            //如果Z端和A端属于同一个虚拟交换机且A端的vlan是重新分配的，那么Z端已经分配的VLAN集合要加上上一步分配的A端vlan
            if(switchUuidA.equals(switchUuidZ) && findVlanForSameVsiAndInterface(msg.getNetWorkUuid(), msg.getInterfaceAUuid()) == null){
                allocatedVlansZ.add(aVlan);
                zVlan = allocateVlan(vlanListZ,allocatedVlansZ);
                if(zVlan == 0){
                    throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
                }
                vo.setzVlan(zVlan);
            }else{
                zVlan = allocateVlan(vlanListZ,allocatedVlansZ);
                if(zVlan == 0){
                    throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下已无可使用的VLAN，请联系系统管理员 "));
                }
                vo.setzVlan(zVlan);
            }

        }

        //如果是独享口并开启Qinq,需要指定内部vlan段
        if(msg.getIsExclusiveA() == 1 && msg.getEnableQinqA() == 1){
            vo.setEnableQinqA(msg.getEnableQinqA());
            List<VlanSegment> vlanSegmentA = msg.getVlanSegmentA();

            for(VlanSegment vlanSegment:vlanSegmentA){
                QinqVO qvo = new QinqVO();
                qvo.setUuid(Platform.getUuid());
                qvo.setTunnelUuid(tunnelUuid);
                qvo.setInterfaceUuid(msg.getInterfaceAUuid());
                qvo.setStartVlan(vlanSegment.getStartVlan());
                qvo.setEndVlan(vlanSegment.getEndVlan());
                dbf.getEntityManager().persist(qvo);
            }
        }else{
            vo.setEnableQinqA(0);
        }
        if(msg.getIsExclusiveZ() == 1 && msg.getEnableQinqZ() == 1){
            vo.setEnableQinqZ(msg.getEnableQinqZ());
            List<VlanSegment> vlanSegmentZ = msg.getVlanSegmentZ();

            for(VlanSegment vlanSegment:vlanSegmentZ){
                QinqVO qvo = new QinqVO();
                qvo.setUuid(Platform.getUuid());
                qvo.setTunnelUuid(tunnelUuid);
                qvo.setInterfaceUuid(msg.getInterfaceZUuid());
                qvo.setStartVlan(vlanSegment.getStartVlan());
                qvo.setEndVlan(vlanSegment.getEndVlan());
                dbf.getEntityManager().persist(qvo);
            }
        }else{
            vo.setEnableQinqZ(0);
        }

        vo.setMonths(msg.getMonths());
        vo.setBandwidth(msg.getBandwidth());
        vo.setDistance(null);
        vo.setState(TunnelState.UNPAID);
        vo.setStatus(TunnelStatus.BREAK);
        vo.setIsMonitor(0);
        vo.setExpiredDate(dbf.getCurrentSqlTime());
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }else{
            vo.setDescription(null);
        }

        dbf.getEntityManager().persist(vo);

        APICreateTunnelEvent evt = new APICreateTunnelEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APICreateTunnelManualMsg msg){
        TunnelVO vo = new TunnelVO();
        String tunnelUuid = Platform.getUuid();
        vo.setUuid(tunnelUuid);

        vo.setAccountUuid(msg.getAccountUuid());
        vo.setNetWorkUuid(msg.getNetWorkUuid());
        vo.setName(msg.getName());
        vo.setInterfaceAUuid(msg.getInterfaceAUuid());
        vo.setInterfaceZUuid(msg.getInterfaceZUuid());
        vo.setaVlan(msg.getaVlan());
        vo.setzVlan(msg.getzVlan());
        if(msg.getEnableQinqA() != null){
            vo.setEnableQinqA(msg.getEnableQinqA());
        }else{
            vo.setEnableQinqA(0);
        }
        if(msg.getEnableQinqZ() != null){
            vo.setEnableQinqZ(msg.getEnableQinqZ());
        }else{
            vo.setEnableQinqZ(0);
        }
        vo.setMonths(msg.getMonths());
        vo.setBandwidth(msg.getBandwidth());
        vo.setDistance(null);
        vo.setState(TunnelState.UNPAID);
        vo.setStatus(TunnelStatus.BREAK);
        vo.setIsMonitor(0);
        vo.setExpiredDate(dbf.getCurrentSqlTime());
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }else{
            vo.setDescription(null);
        }

        if(msg.getVlanSegmentA() != null){
            List<VlanSegment> vlanSegmentA = msg.getVlanSegmentA();

            for(VlanSegment vlanSegment:vlanSegmentA){
                QinqVO qvo = new QinqVO();
                qvo.setUuid(Platform.getUuid());
                qvo.setTunnelUuid(tunnelUuid);
                qvo.setInterfaceUuid(msg.getInterfaceAUuid());
                qvo.setStartVlan(vlanSegment.getStartVlan());
                qvo.setEndVlan(vlanSegment.getEndVlan());
                dbf.getEntityManager().persist(qvo);
            }
        }
        if(msg.getVlanSegmentZ() != null){
            List<VlanSegment> vlanSegmentZ = msg.getVlanSegmentZ();

            for(VlanSegment vlanSegment:vlanSegmentZ){
                QinqVO qvo = new QinqVO();
                qvo.setUuid(Platform.getUuid());
                qvo.setTunnelUuid(tunnelUuid);
                qvo.setInterfaceUuid(msg.getInterfaceZUuid());
                qvo.setStartVlan(vlanSegment.getStartVlan());
                qvo.setEndVlan(vlanSegment.getEndVlan());
                dbf.getEntityManager().persist(qvo);
            }
        }


        dbf.getEntityManager().persist(vo);

        APICreateTunnelManualEvent evt = new APICreateTunnelManualEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateTunnelMsg msg){
        TunnelVO vo = dbf.findByUuid(msg.getUuid(),TunnelVO.class);
        boolean update = false;

        if(msg.getBandwidth() != null){
            vo.setBandwidth(msg.getBandwidth());
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

        //删除对应的QinqVO
        String tunnelUuid = msg.getUuid();
        SimpleQuery<QinqVO> q = dbf.createQuery(QinqVO.class);
        q.add(QinqVO_.tunnelUuid, SimpleQuery.Op.EQ, tunnelUuid);
        List<QinqVO> qinqList = q.list();
        if (qinqList.size() > 0) {
            dbf.removeCollection(qinqList,QinqVO.class);
        }

        APIDeleteTunnelEvent evt = new APIDeleteTunnelEvent(msg.getId());
        evt.setInventory(TunnelInventory.valueOf(vo));
        bus.publish(evt);
    }

    //查询物理接口所属的虚拟交换机
    private String findSwitchByInterface (String interfaceUuid){
        String sql = "select a.switchUuid from SwitchPortVO a,InterfaceVO b " +
                "where a.uuid = b.switchPortUuid " +
                "and b.uuid = :interfaceUuid";
        TypedQuery<String> sq = dbf.getEntityManager().createQuery(sql,String.class);
        sq.setParameter("interfaceUuid",interfaceUuid);
        String switchUuid = sq.getSingleResult();
        return switchUuid;
    }

    //查询该虚拟交换机下所有的Vlan段
    private List<SwitchVlanVO> findSwitchVlanBySwitch (String switchUuid){
        String sql = "select a.uuid, a.switchUuid, a.startVlan, a.endVlan, a.lastOpDate, a.createDate " +
                "from SwitchVlanVO a where a.switchUuid = :switchUuid";
        TypedQuery<SwitchVlanVO> svq = dbf.getEntityManager().createQuery(sql, SwitchVlanVO.class);
        svq.setParameter("switchUuid",switchUuid);
        List<SwitchVlanVO> vlanList = svq.getResultList();
        return vlanList;
    }

    //查询该虚拟交换机下Tunnel已经分配的Vlan
    private List<Integer> fingAllocateVlanBySwitch(String switchUuid){
        String sql = "select distinct a.aVlan as 'vlan' from TunnelVO a,InterfaceVO b,SwitchPortVO c " +
                "where a.interfaceAUuid = b.uuid " +
                "and b.switchPortUuid = c.uuid " +
                "and c.switchUuid = :switchUuid " +
                "union " +
                "select distinct a.zVlan as 'vlan' from TunnelVO a,InterfaceVO b,SwitchPortVO c " +
                "where a.interfaceZUuid = b.uuid " +
                "and b.switchPortUuid = c.uuid " +
                "and c.switchUuid = :switchUuid ";
        TypedQuery<Integer> avq = dbf.getEntityManager().createQuery(sql,Integer.class);
        avq.setParameter("switchUuid",switchUuid);
        List<Integer> allocatedVlans = avq.getResultList();
        return allocatedVlans;
    }

    //查询该端口在同一个VSI下有否存在，如果存在，直接使用该端口的vlan即可
    private Integer findVlanForSameVsiAndInterface(String netWorkUuid, String interfaceUuid){
        String sql = "select distinct a.aVlan as 'vlan' from TunnelVO a " +
                "where a.netWorkUuid = :netWorkUuid and a.interfaceAUuid = :interfaceUuid " +
                "union "+
                "select distinct a.zVlan as 'vlan' from TunnelVO a " +
                "where a.netWorkUuid = :netWorkUuid and a.interfaceZUuid = :interfaceUuid ";
        TypedQuery<Integer> vlanq = dbf.getEntityManager().createQuery(sql,Integer.class);
        vlanq.setParameter("netWorkUuid",netWorkUuid);
        vlanq.setParameter("interfaceUuid",interfaceUuid);
        Integer vlan = vlanq.getSingleResult();
        if(vlan != null){
            return vlan;
        }else{
            return null;
        }
    }

    //分配可用VLAN
    private int allocateVlan(List<SwitchVlanVO> vlanList,List<Integer> allocatedVlans){
        int vlan = 0;
        for (SwitchVlanVO vlanVO : vlanList) {
            Integer startVlan = vlanVO.getStartVlan();
            Integer endVlan = vlanVO.getEndVlan();
            List<Integer> allocatedVlan = null;
            for(Integer alloc : allocatedVlans){
                if(alloc >= startVlan && alloc <= endVlan){
                    allocatedVlan.add(alloc);
                }
            }
            vlan = NetworkUtils.randomAllocateVlan(startVlan,endVlan,allocatedVlan);
            if(vlan == 0){
                continue;
            }else{
                break;
            }
        }
        return vlan;
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
        if(msg instanceof APICreateNetWorkMsg){
            validate((APICreateNetWorkMsg) msg);
        }else if(msg instanceof APICreateNetWorkManualMsg){
            validate((APICreateNetWorkManualMsg) msg);
        }else if(msg instanceof APIUpdateNetWorkMsg){
            validate((APIUpdateNetWorkMsg) msg);
        }else if(msg instanceof APIDeleteNetWorkMsg){
            validate((APIDeleteNetWorkMsg) msg);
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

    private void validate(APICreateNetWorkMsg msg){
        String accountUuid = null;
        if(msg.getAccountUuid() == null){  //---nass
            accountUuid = msg.getSession().getAccountUuid();
        }else{                              //---boss
            accountUuid = msg.getAccountUuid();
        }

        //判断同一个用户的网络名称是否已经存在
        SimpleQuery<NetWorkVO> q = dbf.createQuery(NetWorkVO.class);
        q.add(NetWorkVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(NetWorkVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("network's name %s is already exist ",msg.getName()));
        }
    }

    private void validate(APICreateNetWorkManualMsg msg){
        //判断同一个用户的网络名称是否已经存在
        SimpleQuery<NetWorkVO> q = dbf.createQuery(NetWorkVO.class);
        q.add(NetWorkVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(NetWorkVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("network's name %s is already exist ",msg.getName()));
        }
        //判断VSI是否已经存在
        SimpleQuery<NetWorkVO> q2 = dbf.createQuery(NetWorkVO.class);
        q2.add(NetWorkVO_.vsi, SimpleQuery.Op.EQ, msg.getVsi());
        if(q2.isExists()){
            throw new ApiMessageInterceptionException(argerr("network's vsi %s is already exist ",msg.getVsi()));
        }
    }

    private void validate(APIUpdateNetWorkMsg msg){
        //判断所修改的专有网络是否存在
        SimpleQuery<NetWorkVO> q = dbf.createQuery(NetWorkVO.class);
        q.add(NetWorkVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("NetWork %s is not exist ",msg.getUuid()));
        }

        String accountUuid = null;
        if(msg.getAccountUuid() == null){  //---nass
            accountUuid = msg.getSession().getAccountUuid();
        }else{                              //---boss
            accountUuid = msg.getAccountUuid();
        }
        if(msg.getName() != null){
            //判断同一个用户的网络名称是否已经存在
            SimpleQuery<NetWorkVO> q2 = dbf.createQuery(NetWorkVO.class);
            q2.add(NetWorkVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q2.add(NetWorkVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
            q2.add(NetWorkVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("network's name %s is already exist ",msg.getName()));
            }
        }

    }

    private void validate(APIDeleteNetWorkMsg msg){
        //判断要删除的对象是否存在
        SimpleQuery<NetWorkVO> q = dbf.createQuery(NetWorkVO.class);
        q.add(NetWorkVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("network %s is not exist ",msg.getUuid()));
        }
        //判断该网络是否被专线使用
        SimpleQuery<TunnelVO> q2 = dbf.createQuery(TunnelVO.class);
        q2.add(TunnelVO_.netWorkUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,network is being used!"));
        }

    }

    private void validate(APICreateInterfaceMsg msg){
        String accountUuid = null;
        if(msg.getAccountUuid() == null){  //---nass
            accountUuid = msg.getSession().getAccountUuid();
        }else{                              //---boss
            accountUuid = msg.getAccountUuid();
        }
        //判断同一个用户的接口名称是否已经存在
        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(InterfaceVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
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
        //判断所修改的物理接口是否存在
        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Interface %s is not exist ",msg.getUuid()));
        }

        String accountUuid = null;
        if(msg.getAccountUuid() == null){  //---nass
            accountUuid = msg.getSession().getAccountUuid();
        }else{                              //---boss
            accountUuid = msg.getAccountUuid();
        }
        if(msg.getName() != null){
            //判断同一个用户的网络名称是否已经存在
            SimpleQuery<InterfaceVO> q2 = dbf.createQuery(InterfaceVO.class);
            q2.add(InterfaceVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q2.add(InterfaceVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
            q2.add(InterfaceVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("Interface's name %s is already exist ",msg.getName()));
            }
        }

    }

    private void validate(APIDeleteInterfaceMsg msg){
        //判断所删除的物理接口是否存在
        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Interface %s is not exist ",msg.getUuid()));
        }
        //判断云专线下是否有该物理接口
        SimpleQuery<TunnelVO> q2 = dbf.createQuery(TunnelVO.class);
        q2.add(TunnelVO_.interfaceAUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,interface is being used by tunnel!"));
        }
        SimpleQuery<TunnelVO> q3 = dbf.createQuery(TunnelVO.class);
        q3.add(TunnelVO_.interfaceZUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q3.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,interface is being used by tunnel!"));
        }

    }

    private void validate(APICreateTunnelMsg msg){
        String accountUuid = null;
        if(msg.getAccountUuid() == null){  //---nass
            accountUuid = msg.getSession().getAccountUuid();
        }else{                              //---boss
            accountUuid = msg.getAccountUuid();
        }
        //判断同一个用户和同一个专有网络下的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        q.add(TunnelVO_.netWorkUuid, SimpleQuery.Op.EQ, msg.getNetWorkUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ",msg.getName()));
        }
        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.interfaceUuid = :interfaceUuid " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        if(msg.getVlanSegmentA() != null){
            List<VlanSegment> vlanSegmentA = msg.getVlanSegmentA();
            for(VlanSegment vlanSegment:vlanSegmentA){
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

        if(msg.getVlanSegmentZ() != null){
            List<VlanSegment> vlanSegmentZ = msg.getVlanSegmentZ();
            for(VlanSegment vlanSegment:vlanSegmentZ){
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

    private void validate(APICreateTunnelManualMsg msg){

        //判断同一个用户和同一个专有网络下的名称是否已经存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        q.add(TunnelVO_.netWorkUuid, SimpleQuery.Op.EQ, msg.getNetWorkUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("Tunnel's name %s is already exist ",msg.getName()));
        }

        //判断外部VLAN合法性
        if(msg.getaVlan() < 1 || msg.getzVlan() < 1){
            throw new ApiMessageInterceptionException(argerr("Vlan Minimum is 1"));
        }
        if(msg.getaVlan() > 4094 || msg.getzVlan() < 1){
            throw new ApiMessageInterceptionException(argerr("vlan maximum  is 4094"));
        }

        //查询该TUNNEL的物理接口所属的虚拟交换机
        String switchUuidA = findSwitchByInterface(msg.getInterfaceAUuid());
        String switchUuidZ = findSwitchByInterface(msg.getInterfaceZUuid());

        //查询该虚拟交换机下所有的Vlan段
        List<SwitchVlanVO> vlanListA = findSwitchVlanBySwitch(switchUuidA);
        List<SwitchVlanVO> vlanListZ = findSwitchVlanBySwitch(switchUuidZ);

        //查询该虚拟交换机下已经分配的Vlan
        List<Integer> allocatedVlansA = fingAllocateVlanBySwitch(switchUuidA);
        List<Integer> allocatedVlansZ = fingAllocateVlanBySwitch(switchUuidZ);

        //判断外部VLAN是否在该虚拟交换机的VLAN段中
        Boolean innerA = false;
        for(SwitchVlanVO switchVlanVO:vlanListA){
            if(msg.getaVlan() >= switchVlanVO.getStartVlan() && msg.getaVlan() <= switchVlanVO.getEndVlan()){
                innerA = true;
                break;
            }
        }
        if(innerA == false){
            throw new ApiMessageInterceptionException(argerr("avlan not in switchVlan"));
        }
        Boolean innerZ = false;
        for(SwitchVlanVO switchVlanVO:vlanListZ){
            if(msg.getzVlan() >= switchVlanVO.getStartVlan() && msg.getzVlan() <= switchVlanVO.getEndVlan()){
                innerZ = true;
                break;
            }
        }
        if(innerZ == false){
            throw new ApiMessageInterceptionException(argerr("zvlan not in switchVlan"));
        }

        //判断外部vlan是否可用
            //给A端口vlan验证
            //同一个VSI下同一个物理接口不用分配vlan，他们vlan一样
        Integer aVlan = null;
        aVlan = findVlanForSameVsiAndInterface(msg.getNetWorkUuid(), msg.getInterfaceAUuid());
        if(aVlan != null){
            if(aVlan != msg.getaVlan()){
                throw new ApiMessageInterceptionException(argerr("该端口在同一专有网络下已经分配vlan，请使用该vlan %s ",aVlan));
            }
        }else{
            if(allocatedVlansA.contains(msg.getaVlan())){
                throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用",msg.getaVlan()));
            }
        }
            //给Z端口vlan验证
            //同一个VSI下同一个物理接口不用分配vlan，他们vlan一样
        Integer zVlan = null;
        zVlan = findVlanForSameVsiAndInterface(msg.getNetWorkUuid(), msg.getInterfaceZUuid());
        if(zVlan != null){
            if(zVlan != msg.getzVlan()){
                throw new ApiMessageInterceptionException(argerr("该端口在同一专有网络下已经分配vlan，请使用该vlan %s ",zVlan));
            }
        }else{
            //如果Z端和A端属于同一个虚拟交换机且A端的vlan是重新分配的，那么Z端已经分配的VLAN集合要加上上一步分配的A端vlan
            if(switchUuidA.equals(switchUuidZ) && findVlanForSameVsiAndInterface(msg.getNetWorkUuid(), msg.getInterfaceAUuid()) == null){
                allocatedVlansZ.add(msg.getaVlan());
                if(allocatedVlansZ.contains(msg.getzVlan())){
                    throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用",msg.getzVlan()));
                }

            }else{
                if(allocatedVlansZ.contains(msg.getzVlan())){
                    throw new ApiMessageInterceptionException(argerr("该vlan %s 已经被占用",msg.getzVlan()));
                }
            }

        }


        //判断同一个switchPort下内部VLAN段是否有重叠
        String sql = "select count(a.uuid) from QinqVO a " +
                "where a.interfaceUuid = :interfaceUuid " +
                "and ((a.startVlan between :startVlan and :endVlan) " +
                "or (a.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between a.startVlan and a.endVlan) " +
                "or (:endVlan between a.startVlan and a.endVlan))";
        if(msg.getVlanSegmentA() != null){
            List<VlanSegment> vlanSegmentA = msg.getVlanSegmentA();
            for(VlanSegment vlanSegment:vlanSegmentA){
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

        if(msg.getVlanSegmentZ() != null){
            List<VlanSegment> vlanSegmentZ = msg.getVlanSegmentZ();
            for(VlanSegment vlanSegment:vlanSegmentZ){
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

    private void validate(APIUpdateTunnelMsg msg){
        //判断所修改的Tunnel是否存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel %s is not exist ",msg.getUuid()));
        }

    }

    private void validate(APIDeleteTunnelMsg msg){
        //判断所删除的Tunnel是否存在
        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Tunnel %s is not exist ",msg.getUuid()));
        }
    }
}
