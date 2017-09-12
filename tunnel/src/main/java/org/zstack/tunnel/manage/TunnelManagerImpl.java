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
import org.zstack.tunnel.header.endpoint.EndpointType;
import org.zstack.tunnel.header.tunnel.*;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;
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
        }else if(msg instanceof APIUpdateNetWorkMsg){
            handle((APIUpdateNetWorkMsg) msg);
        }else if(msg instanceof APICreateInterfaceNassMsg){
            handle((APICreateInterfaceNassMsg) msg);
        }else if(msg instanceof APICreateInterfaceBossMsg){
            handle((APICreateInterfaceBossMsg) msg);
        }else if(msg instanceof APICreateTunnelMsg){
            handle((APICreateTunnelMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateNetWorkMsg msg){
        NetWorkVO vo = new NetWorkVO();

        vo.setUuid(Platform.getUuid());
        if(msg.getAccountUuid() == null){   //---nass
            vo.setAccountUuid(msg.getSession().getAccountUuid());

            String sql = "select max(vo.vsi) from NetWorkVO vo";
            TypedQuery<Integer> vq = dbf.getEntityManager().createQuery(sql, Integer.class);
            Integer vsi = vq.getSingleResult();
            vo.setVsi(vsi+1);

        }else{                              //---boss
            vo.setAccountUuid(msg.getAccountUuid());
            vo.setVsi(msg.getVsi());
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

    private void handle(APICreateInterfaceNassMsg msg){
        InterfaceVO vo = new InterfaceVO();

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getSession().getAccountUuid());
        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());
        EndpointType endpointType = msg.getEndpointType();
        vo.setIsExclusive(msg.getIsExclusive());

        String portUuid = null;

        if(endpointType.equals(EndpointType.CLOUD) && msg.getIsExclusive() ==0){  //云共享
            String sql = "select c.uuid from EndpointVO a,SwitchVO b,SwitchPortVO c " +
                    "where a.uuid = b.endpointUuid and b.uuid = c.switchUuid " +
                    "and a.uuid = '"+msg.getEndpointUuid()+"' and a.enabled = 1 and a.status = 'NORMAL' and a.openToCustomers = 1 " +
                    "and b.enabled = 1 and b.status = 'NORMAL' " +
                    "and c.isExclusive = 0 and c.enabled = 1";
            Query vq = dbf.getEntityManager().createNativeQuery(sql);
            List<String> portList = vq.getResultList();
            Random r = new Random();
            portUuid = portList.get(r.nextInt(portList.size()));

        }else if(endpointType.equals(EndpointType.CLOUD) && msg.getIsExclusive() ==1){ //云独享
            String sql = "select c.uuid from EndpointVO a, SwitchVO b,SwitchPortVO c " +
                    "where a.uuid = b.endpointUuid and b.uuid = c.switchUuid " +
                    "and a.uuid = '"+msg.getEndpointUuid()+"' and a.enabled = 1 and a.status = 'NORMAL' and a.openToCustomers = 1 " +
                    "and b.enabled = 1 and b.status = 'NORMAL' " +
                    "and c.isExclusive = 1 and c.enabled = 1 and c.portType = '"+msg.getPortType().toString()+"' " +
                    "and c.uuid not in (select switchPortUuid from InterfaceVO)";
            Query vq = dbf.getEntityManager().createNativeQuery(sql);
            List<String> portList = vq.getResultList();
            Random r = new Random();
            portUuid = portList.get(r.nextInt(portList.size()));

        }else if(endpointType.equals(EndpointType.ACCESSIN) && msg.getIsExclusive() ==1){ //接入独享
            String sql = "select c.uuid from EndpointVO a,SwitchVO b,SwitchPortVO c " +
                    "where a.uuid = b.endpointUuid and b.uuid = c.switchUuid " +
                    "and a.uuid = '"+msg.getEndpointUuid()+"' and a.enabled = 1 and a.status = 'NORMAL' and a.openToCustomers = 1 " +
                    "and b.enabled = 1 and b.status = 'NORMAL' " +
                    "and c.isExclusive = 1 and c.enabled = 1 and c.portType = '"+msg.getPortType().toString()+"' " +
                    "and c.uuid not in (select switchPortUuid from InterfaceVO)";
            Query vq = dbf.getEntityManager().createNativeQuery(sql);
            List<String> portList = vq.getResultList();
            Random r = new Random();
            portUuid = portList.get(r.nextInt(portList.size()));
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

        APICreateInterfaceNassEvent evt = new APICreateInterfaceNassEvent(msg.getId());
        evt.setInventory(InterfaceInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateInterfaceBossMsg msg){
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

        APICreateInterfaceBossEvent evt = new APICreateInterfaceBossEvent(msg.getId());
        evt.setInventory(InterfaceInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateTunnelMsg msg){
        TunnelVO vo = new TunnelVO();

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getSession().getAccountUuid());
        vo.setNetWorkUuid(msg.getNetWorkUuid());
        vo.setName(msg.getName());
        vo.setInterfaceAUuid(msg.getInterfaceAUuid());
        vo.setInterfaceZUuid(msg.getInterfaceZUuid());

        if(msg.getIsExclusiveA() == 0){     //共享口，不能开启QINQ,策略分配一个外部VLAN
            vo.setEnableQinqA(0);

        }else{                              //独享口
            if(msg.getEnableQinqA() == 0){  //不开启QINQ，策略分配一个外部VLAN
                vo.setEnableQinqA(0);

            }else{                          //开启QINQ,策略分配一个外部VLAN，用户指定至少一个内部VLAN段
                vo.setEnableQinqA(1);
            }
        }
        if(msg.getIsExclusiveZ() == 0){     //共享口，不能开启QINQ,策略分配一个外部VLAN
            vo.setEnableQinqZ(0);

        }else{                              //独享口
            if(msg.getEnableQinqZ() == 0){  //不开启QINQ，策略分配一个外部VLAN
                vo.setEnableQinqZ(0);

            }else{                          //开启QINQ,策略分配一个外部VLAN，用户指定至少一个内部VLAN段
                vo.setEnableQinqZ(1);
            }
        }

        vo.setMonths(msg.getMonths());
        vo.setBandwidth(msg.getBandwidth());
        vo.setDistance(null);
        vo.setState(TunnelState.UNPAID);
        vo.setStatus(TunnelStatus.BREAK);
        vo.setIsMonitor(0);
        vo.setExpiredDate(null);
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }else{
            vo.setDescription(null);
        }

        vo = dbf.persistAndRefresh(vo);

        APICreateTunnelNassEvent evt = new APICreateTunnelNassEvent(msg.getId());
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
        if(msg instanceof APICreateNetWorkMsg){
            validate((APICreateNetWorkMsg) msg);
        }else if(msg instanceof APIUpdateNetWorkMsg){
            validate((APIUpdateNetWorkMsg) msg);
        }else if(msg instanceof APICreateInterfaceNassMsg){
            validate((APICreateInterfaceNassMsg) msg);
        }else if(msg instanceof APICreateInterfaceBossMsg){
            validate((APICreateInterfaceBossMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateNetWorkMsg msg){
        //判断同一个用户的网络名称是否已经存在,如果是BOSS，还要判断VSI是否已经被使用
        if(msg.getAccountUuid() == null){   //---nass
            SimpleQuery<NetWorkVO> q = dbf.createQuery(NetWorkVO.class);
            q.add(NetWorkVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q.add(NetWorkVO_.accountUuid, SimpleQuery.Op.EQ, msg.getSession().getAccountUuid());
            if(q.isExists()){
                throw new ApiMessageInterceptionException(argerr("network's name %s is already exist ",msg.getName()));
            }

        }else{                              //---boss
            SimpleQuery<NetWorkVO> q2 = dbf.createQuery(NetWorkVO.class);
            q2.add(NetWorkVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q2.add(NetWorkVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("network's name %s is already exist ",msg.getName()));
            }
            SimpleQuery<NetWorkVO> q3 = dbf.createQuery(NetWorkVO.class);
            q3.add(NetWorkVO_.vsi, SimpleQuery.Op.EQ, msg.getVsi());
            if(q3.isExists()){
                throw new ApiMessageInterceptionException(argerr("vsi %s is already exist ",msg.getVsi()));
            }

        }

    }

    private void validate(APIUpdateNetWorkMsg msg){
        //判断所修改的专有网络是否存在
        SimpleQuery<NetWorkVO> q = dbf.createQuery(NetWorkVO.class);
        q.add(NetWorkVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("NetWork %s is not exist ",msg.getUuid()));
        }

        //判断同一个用户的网络名称是否已经存在
        if(msg.getAccountUuid() == null){   //---nass
            SimpleQuery<NetWorkVO> q2 = dbf.createQuery(NetWorkVO.class);
            q2.add(NetWorkVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q2.add(NetWorkVO_.accountUuid, SimpleQuery.Op.EQ, msg.getSession().getAccountUuid());
            q2.add(NetWorkVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("network's name %s is already exist ",msg.getName()));
            }

        }else{                              //---boss
            SimpleQuery<NetWorkVO> q3 = dbf.createQuery(NetWorkVO.class);
            q3.add(NetWorkVO_.name, SimpleQuery.Op.EQ, msg.getName());
            q3.add(NetWorkVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            q3.add(NetWorkVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q3.isExists()){
                throw new ApiMessageInterceptionException(argerr("network's name %s is already exist ",msg.getName()));
            }


        }

    }

    private void validate(APICreateInterfaceNassMsg msg){
        //判断同一个用户的接口名称是否已经存在

        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(InterfaceVO_.accountUuid, SimpleQuery.Op.EQ, msg.getSession().getAccountUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("Interface's name %s is already exist ",msg.getName()));
        }
    }

    private void validate(APICreateInterfaceBossMsg msg){
        //判断同一个用户的接口名称是否已经存在

        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q.add(InterfaceVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("Interface's name %s is already exist ",msg.getName()));
        }
    }


}
