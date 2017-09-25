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
import org.zstack.tunnel.header.endpoint.EndpointVO;
import org.zstack.tunnel.header.endpoint.EndpointVO_;
import org.zstack.tunnel.header.node.NodeVO;
import org.zstack.tunnel.header.node.NodeVO_;
import org.zstack.tunnel.header.switchs.*;
import org.zstack.tunnel.header.tunnel.InterfaceVO;
import org.zstack.tunnel.header.tunnel.InterfaceVO_;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.TypedQuery;

import java.util.List;

import static org.zstack.core.Platform.argerr;

/**
 * Created by DCY on 2017-09-07
 */
public class SwitchManagerImpl  extends AbstractService implements SwitchManager,ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(SwitchManagerImpl.class);

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
        if(msg instanceof APICreateSwitchModelMsg){
            handle((APICreateSwitchModelMsg) msg);
        }else if(msg instanceof APIDeleteSwitchModelMsg){
            handle((APIDeleteSwitchModelMsg) msg);
        }else if(msg instanceof APICreatePhysicalSwitchMsg){
            handle((APICreatePhysicalSwitchMsg) msg);
        }else if(msg instanceof APIUpdatePhysicalSwitchMsg){
            handle((APIUpdatePhysicalSwitchMsg) msg);
        }else if(msg instanceof APIDeletePhysicalSwitchMsg){
            handle((APIDeletePhysicalSwitchMsg) msg);
        }else if(msg instanceof APICreateSwitchMsg){
            handle((APICreateSwitchMsg) msg);
        }else if(msg instanceof APIUpdateSwitchMsg){
            handle((APIUpdateSwitchMsg) msg);
        }else if(msg instanceof APIDeleteSwitchMsg){
            handle((APIDeleteSwitchMsg) msg);
        }else if(msg instanceof APICreateSwitchPortMsg){
            handle((APICreateSwitchPortMsg) msg);
        }else if(msg instanceof APIUpdateSwitchPortMsg){
            handle((APIUpdateSwitchPortMsg) msg);
        }else if(msg instanceof APIDeleteSwitchPortMsg){
            handle((APIDeleteSwitchPortMsg) msg);
        }else if(msg instanceof APICreateSwitchVlanMsg){
            handle((APICreateSwitchVlanMsg) msg);
        }else if(msg instanceof APIDeleteSwitchVlanMsg){
            handle((APIDeleteSwitchVlanMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateSwitchModelMsg msg){
        SwitchModelVO vo = new SwitchModelVO();

        vo.setUuid(Platform.getUuid());
        vo.setBrand(msg.getBrand());
        vo.setModel(msg.getModel());
        vo.setSubModel(msg.getSubModel());

        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchModelEvent evt = new APICreateSwitchModelEvent(msg.getId());
        evt.setInventory(SwitchModelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteSwitchModelMsg msg){
        String uuid = msg.getUuid();
        SwitchModelVO vo = dbf.findByUuid(uuid,SwitchModelVO.class);

        if (vo != null) {
            dbf.remove(vo);
        }

        APIDeleteSwitchModelEvent evt = new APIDeleteSwitchModelEvent(msg.getId());
        evt.setInventory(SwitchModelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreatePhysicalSwitchMsg msg){
        PhysicalSwitchVO vo = new PhysicalSwitchVO();

        vo.setUuid(Platform.getUuid());
        vo.setNodeUuid(msg.getNodeUuid());
        vo.setSwitchModelUuid(msg.getSwitchModelUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setOwner(msg.getOwner());
        vo.setType(msg.getType());
        vo.setRack(msg.getRack());
        vo.setmIP(msg.getmIP());
        vo.setLocalIP(msg.getLocalIP());
        vo.setUsername(msg.getUsername());
        vo.setPassword(msg.getPassword());
        vo.setDescription(msg.getDescription());

        vo = dbf.persistAndRefresh(vo);

        APICreatePhysicalSwitchEvent evt = new APICreatePhysicalSwitchEvent(msg.getId());
        evt.setInventory(PhysicalSwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdatePhysicalSwitchMsg msg){
        PhysicalSwitchVO vo = dbf.findByUuid(msg.getUuid(),PhysicalSwitchVO.class);
        boolean update = false;

        if(msg.getSwitchModelUuid() != null){
            vo.setSwitchModelUuid(msg.getSwitchModelUuid());
            update = true;
        }
        if(msg.getCode() != null){
            vo.setCode(msg.getCode());
            update = true;
        }
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getOwner() != null){
            vo.setOwner(msg.getOwner());
            update = true;
        }
        if(msg.getType() != null){
            vo.setType(msg.getType());
            update = true;
        }
        if(msg.getRack() != null){
            vo.setRack(msg.getRack());
            update = true;
        }
        if(msg.getmIP() != null){
            vo.setmIP(msg.getmIP());
            update = true;
        }
        if(msg.getLocalIP() != null){
            vo.setLocalIP(msg.getLocalIP());
            update = true;
        }
        if(msg.getUsername() != null){
            vo.setUsername(msg.getUsername());
            update = true;
        }
        if(msg.getPassword() != null){
            vo.setPassword(msg.getPassword());
            update = true;
        }
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdatePhysicalSwitchEvent evt = new APIUpdatePhysicalSwitchEvent(msg.getId());
        evt.setInventory(PhysicalSwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeletePhysicalSwitchMsg msg){
        PhysicalSwitchEO eo = dbf.findByUuid(msg.getUuid(),PhysicalSwitchEO.class);
        PhysicalSwitchVO vo = dbf.findByUuid(msg.getUuid(),PhysicalSwitchVO.class);

        eo.setDeleted(1);

        eo = dbf.updateAndRefresh(eo);

        APIDeletePhysicalSwitchEvent evt = new APIDeletePhysicalSwitchEvent(msg.getId());
        evt.setInventory(PhysicalSwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateSwitchMsg msg){
        SwitchVO vo = new SwitchVO();

        vo.setUuid(Platform.getUuid());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setPhysicalSwitchUuid(msg.getPhysicalSwitchUuid());
        vo.setUpperType(msg.getUpperType());
        vo.setState(SwitchState.Enabled);
        vo.setStatus(SwitchStatus.Connected);
        vo.setDescription(msg.getDescription());


        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchEvent evt = new APICreateSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateSwitchMsg msg){
        SwitchVO vo = dbf.findByUuid(msg.getUuid(),SwitchVO.class);
        boolean update = false;
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getCode() != null){
            vo.setCode(msg.getCode());
            update = true;
        }
        if(msg.getPhysicalSwitchUuid() != null){
            vo.setPhysicalSwitchUuid(msg.getPhysicalSwitchUuid());
            update = true;
        }
        if(msg.getUpperType() != null){
            vo.setUpperType(msg.getUpperType());
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
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateSwitchEvent evt = new APIUpdateSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteSwitchMsg msg){
        SwitchEO eo = dbf.findByUuid(msg.getUuid(),SwitchEO.class);
        SwitchVO vo = dbf.findByUuid(msg.getUuid(),SwitchVO.class);

        eo.setDeleted(1);

        eo = dbf.updateAndRefresh(eo);

        APIDeleteSwitchEvent evt = new APIDeleteSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateSwitchPortMsg msg){
        SwitchPortVO vo = new SwitchPortVO();

        vo.setUuid(Platform.getUuid());
        vo.setSwitchUuid(msg.getSwitchUuid());
        vo.setPortNum(null);
        vo.setPortName(msg.getPortName());
        vo.setPortType(msg.getPortType());
        vo.setPortAttribute(msg.getPortAttribute());
        vo.setState(SwitchPortState.Enabled);

        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchPortEvent evt = new APICreateSwitchPortEvent(msg.getId());
        evt.setInventory(SwitchPortInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateSwitchPortMsg msg){
        SwitchPortVO vo = dbf.findByUuid(msg.getUuid(),SwitchPortVO.class);
        boolean update = false;
        if(msg.getState() != null){
            vo.setState(msg.getState());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateSwitchPortEvent evt = new APIUpdateSwitchPortEvent(msg.getId());
        evt.setInventory(SwitchPortInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteSwitchPortMsg msg){
        String uuid = msg.getUuid();
        SwitchPortVO vo = dbf.findByUuid(uuid,SwitchPortVO.class);

        if (vo != null) {
            dbf.remove(vo);
        }

        APIDeleteSwitchPortEvent evt = new APIDeleteSwitchPortEvent(msg.getId());
        evt.setInventory(SwitchPortInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateSwitchVlanMsg msg){
        SwitchVlanVO vo = new SwitchVlanVO();

        vo.setUuid(Platform.getUuid());
        vo.setSwitchUuid(msg.getSwitchUuid());
        vo.setStartVlan(msg.getStartVlan());
        vo.setEndVlan(msg.getEndVlan());

        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchVlanEvent evt = new APICreateSwitchVlanEvent(msg.getId());
        evt.setInventory(SwitchVlanInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteSwitchVlanMsg msg){
        String uuid = msg.getUuid();
        SwitchVlanVO vo = dbf.findByUuid(uuid,SwitchVlanVO.class);

        if (vo != null) {
            dbf.remove(vo);
        }

        APIDeleteSwitchVlanEvent evt = new APIDeleteSwitchVlanEvent(msg.getId());
        evt.setInventory(SwitchVlanInventory.valueOf(vo));
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
        return bus.makeLocalServiceId(SwitchConstant.SERVICE_ID);
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if(msg instanceof APICreateSwitchModelMsg){
            validate((APICreateSwitchModelMsg) msg);
        }else if(msg instanceof APIDeleteSwitchModelMsg){
            validate((APIDeleteSwitchModelMsg) msg);
        }else if(msg instanceof APICreatePhysicalSwitchMsg){
            validate((APICreatePhysicalSwitchMsg) msg);
        }else if(msg instanceof APIUpdatePhysicalSwitchMsg){
            validate((APIUpdatePhysicalSwitchMsg) msg);
        }else if(msg instanceof APIDeletePhysicalSwitchMsg){
            validate((APIDeletePhysicalSwitchMsg) msg);
        }else if(msg instanceof APICreateSwitchMsg){
            validate((APICreateSwitchMsg) msg);
        }else if(msg instanceof APIUpdateSwitchMsg){
            validate((APIUpdateSwitchMsg) msg);
        }else if(msg instanceof APIDeleteSwitchMsg){
            validate((APIDeleteSwitchMsg) msg);
        }else if(msg instanceof APICreateSwitchPortMsg){
            validate((APICreateSwitchPortMsg) msg);
        }else if(msg instanceof APIUpdateSwitchPortMsg){
            validate((APIUpdateSwitchPortMsg) msg);
        }else if(msg instanceof APIDeleteSwitchPortMsg){
            validate((APIDeleteSwitchPortMsg) msg);
        }else if(msg instanceof APICreateSwitchVlanMsg){
            validate((APICreateSwitchVlanMsg) msg);
        }else if(msg instanceof APIDeleteSwitchVlanMsg){
            validate((APIDeleteSwitchVlanMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateSwitchModelMsg msg){
        //判断model是否已经存在
        if(msg.getSubModel() == null){
            SimpleQuery<SwitchModelVO> q = dbf.createQuery(SwitchModelVO.class);
            q.add(SwitchModelVO_.model, SimpleQuery.Op.EQ, msg.getModel());
            if(q.isExists()){
                throw new ApiMessageInterceptionException(argerr("switchModel %s is already exist ",msg.getModel()));
            }
        }else{
            SimpleQuery<SwitchModelVO> q = dbf.createQuery(SwitchModelVO.class);
            q.add(SwitchModelVO_.model, SimpleQuery.Op.EQ, msg.getModel());
            q.add(SwitchModelVO_.subModel, SimpleQuery.Op.EQ, msg.getSubModel());
            if(q.isExists()){
                throw new ApiMessageInterceptionException(argerr("switchModel %s is already exist ",msg.getModel()));
            }
        }

    }

    private void validate(APIDeleteSwitchModelMsg msg){
        //判断该型号是否被物理交换机使用
        SimpleQuery<PhysicalSwitchVO> q = dbf.createQuery(PhysicalSwitchVO.class);
        q.add(PhysicalSwitchVO_.switchModelUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,switchModel is being used!"));
        }

    }

    private void validate(APICreatePhysicalSwitchMsg msg){
        //判断code是否已经存在
        SimpleQuery<PhysicalSwitchVO> q = dbf.createQuery(PhysicalSwitchVO.class);
        q.add(PhysicalSwitchVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's code %s is already exist ",msg.getCode()));
        }
        //判断mIP和LocalIp唯一性
        SimpleQuery<PhysicalSwitchVO> q2 = dbf.createQuery(PhysicalSwitchVO.class);
        q2.add(PhysicalSwitchVO_.mIP, SimpleQuery.Op.EQ, msg.getmIP());
        if(q2.isExists()){
            throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's mip %s is already exist ",msg.getmIP()));
        }
        SimpleQuery<PhysicalSwitchVO> q3 = dbf.createQuery(PhysicalSwitchVO.class);
        q3.add(PhysicalSwitchVO_.localIP, SimpleQuery.Op.EQ, msg.getLocalIP());
        if(q3.isExists()){
            throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's localIp %s is already exist ",msg.getLocalIP()));
        }

    }

    private void validate(APIUpdatePhysicalSwitchMsg msg){

        //判断code是否已经存在
        if(msg.getCode() != null){
            SimpleQuery<PhysicalSwitchVO> q = dbf.createQuery(PhysicalSwitchVO.class);
            q.add(PhysicalSwitchVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q.add(PhysicalSwitchVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q.isExists()){
                throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's code %s is already exist ",msg.getCode()));
            }
        }

        //判断mIP和LocalIp唯一性
        if(msg.getmIP() != null){
            SimpleQuery<PhysicalSwitchVO> q2 = dbf.createQuery(PhysicalSwitchVO.class);
            q2.add(PhysicalSwitchVO_.mIP, SimpleQuery.Op.EQ, msg.getmIP());
            q2.add(PhysicalSwitchVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's mip %s is already exist ",msg.getmIP()));
            }
        }
        if(msg.getLocalIP() != null){
            SimpleQuery<PhysicalSwitchVO> q3 = dbf.createQuery(PhysicalSwitchVO.class);
            q3.add(PhysicalSwitchVO_.localIP, SimpleQuery.Op.EQ, msg.getLocalIP());
            q3.add(PhysicalSwitchVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q3.isExists()){
                throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's localIp %s is already exist ",msg.getLocalIP()));
            }
        }

    }

    private void validate(APIDeletePhysicalSwitchMsg msg){

        //判断该物理交换机下是否有虚拟交换机
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.physicalSwitchUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,PhysicalSwitch is being used!"));
        }
    }

    private void validate(APICreateSwitchMsg msg){
        //判断code是否已经存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("switch's code %s is already exist ",msg.getCode()));
        }

    }

    private void validate(APIUpdateSwitchMsg msg){
        //判断code是否已经存在
        if(msg.getCode() != null){
            SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
            q.add(SwitchVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q.add(SwitchVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q.isExists()){
                throw new ApiMessageInterceptionException(argerr("switch's code %s is already exist ",msg.getCode()));
            }
        }

    }

    private void validate(APIDeleteSwitchMsg msg){

        //判断该交换机下是否有端口
        SimpleQuery<SwitchPortVO> q = dbf.createQuery(SwitchPortVO.class);
        q.add(SwitchPortVO_.switchUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,Switch is being used by switchPort!"));
        }
        //判断该交换机下是否有Vlan段
        SimpleQuery<SwitchVlanVO> q2 = dbf.createQuery(SwitchVlanVO.class);
        q2.add(SwitchVlanVO_.switchUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,Switch is being used by switchVlan!"));
        }
    }


    private void validate(APICreateSwitchPortMsg msg){

        //端口名称在一个物理交换机下是否存在
        String sql = "select count(a.uuid) from PhysicalSwitchVO a,SwitchVO b,SwitchPortVO c " +
                "where a.uuid = b.physicalSwitchUuid " +
                "and b.uuid = c.switchUuid " +
                "and c.portName = :portName " +
                "and a.uuid = (select physicalSwitchUuid from SwitchVO where uuid = :switchUuid)";
        TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
        vq.setParameter("portName",msg.getPortName());
        vq.setParameter("switchUuid",msg.getSwitchUuid());
        Long count = vq.getSingleResult();
        if(count>0){
            throw new ApiMessageInterceptionException(argerr("portName %s is already exist ",msg.getPortName()));
        }
    }

    private void validate(APIUpdateSwitchPortMsg msg){ }

    private void validate(APIDeleteSwitchPortMsg msg){

        //判断该端口是否被买了
        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.switchPortUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,switchPort is being used!"));
        }

    }

    private void validate(APICreateSwitchVlanMsg msg){

        if(msg.getStartVlan() > msg.getEndVlan()){
            throw new ApiMessageInterceptionException(argerr("endvlan must more than startvlan"));
        }
        //同一个物理交换机下的VLAN不能重叠
        String sql = "select count(a.uuid) from PhysicalSwitchVO a,SwitchVO b,SwitchVlanVO c " +
                "where a.uuid = b.physicalSwitchUuid and b.uuid = c.switchUuid " +
                "and a.uuid = (select physicalSwitchUuid from SwitchVO where uuid = :switchUuid) " +
                "and ((c.startVlan between :startVlan and :endVlan) " +
                "or (c.endVlan between :startVlan and :endVlan) " +
                "or (:startVlan between c.startVlan and c.endVlan) " +
                "or (:endVlan between c.startVlan and c.endVlan))";
        TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
        vq.setParameter("switchUuid",msg.getSwitchUuid());
        vq.setParameter("startVlan",msg.getStartVlan());
        vq.setParameter("endVlan",msg.getEndVlan());
        Long count = vq.getSingleResult();
        if(count > 0){
            throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
        }
    }

    private void validate(APIDeleteSwitchVlanMsg msg){
        //判断该Vlan段有没有被使用
        SwitchVlanVO vo = dbf.findByUuid(msg.getUuid(),SwitchVlanVO.class);
        List<Integer> vlanList = fingAllocateVlanBySwitch(vo.getSwitchUuid());
        if(vlanList.size() > 0){
            for(Integer vlan : vlanList){
                if(vlan >= vo.getStartVlan() && vlan <= vo.getEndVlan()){
                    throw new ApiMessageInterceptionException(argerr("cannot delete,switchVlan is being used!"));
                }
            }
        }
    }

    //查询该虚拟交换机下Tunnel已经分配的Vlan
    private List<Integer> fingAllocateVlanBySwitch(String switchUuid){
        String sql = "select distinct a.vlan from TunnelInterfaceVO a,InterfaceVO b,SwitchPortVO c " +
                "where a.interfaceUuid = b.uuid " +
                "and b.switchPortUuid = c.uuid " +
                "and c.switchUuid = :switchUuid ";
        TypedQuery<Integer> avq = dbf.getEntityManager().createQuery(sql,Integer.class);
        avq.setParameter("switchUuid",switchUuid);
        List<Integer> allocatedVlans = avq.getResultList();
        return allocatedVlans;
    }
}
