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
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.tunnel.header.endpoint.*;
import org.zstack.tunnel.header.node.*;
import org.zstack.tunnel.header.switchs.*;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

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
        if(msg instanceof APICreateNodeMsg){                //---------handleApiMessage-NODE----------------------------
            handle((APICreateNodeMsg) msg);
        }else if(msg instanceof APIUpdateNodeMsg){
            handle((APIUpdateNodeMsg) msg);
        }else if(msg instanceof APICreateEndpointMsg){      //---------handleApiMessage-ENDPOINT------------------------
            handle((APICreateEndpointMsg) msg);
        }else if(msg instanceof APIUpdateEndpointMsg){
            handle((APIUpdateEndpointMsg) msg);
        }else if(msg instanceof APIDisableEndpointMsg){
            handle((APIDisableEndpointMsg) msg);
        }else if(msg instanceof APIEnableEndpointMsg){
            handle((APIEnableEndpointMsg) msg);
        }else if(msg instanceof APICloseEndpointMsg){
            handle((APICloseEndpointMsg) msg);
        }else if(msg instanceof APIOpenEndpointMsg){
            handle((APIOpenEndpointMsg) msg);
        }else if(msg instanceof APICreateSwitchMsg){        //---------handleApiMessage-SWITCH--------------------------
            handle((APICreateSwitchMsg) msg);
        }else if(msg instanceof APIUpdateSwitchMsg){
            handle((APIUpdateSwitchMsg) msg);
        }else if(msg instanceof APIEnableSwitchMsg){
            handle((APIEnableSwitchMsg) msg);
        }else if(msg instanceof APIDisableSwitchMsg){
            handle((APIDisableSwitchMsg) msg);
        }else if(msg instanceof APIPrivateSwitchMsg){
            handle((APIPrivateSwitchMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    //-----------------------------------------------------HANDLE-NODE--------------------------------------------------
    private void handle(APICreateNodeMsg msg){
        NodeVO vo = new NodeVO();

        vo.setUuid(Platform.getUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setLongtitude(msg.getLongtitude());
        vo.setLatitude(msg.getLatitude());
        vo.setProperty(msg.getProperty());
        vo.setProvince(msg.getProvince());
        vo.setCity(msg.getCity());
        vo.setAddress(msg.getAddress());
        vo.setContact(msg.getContact());
        vo.setTelephone(msg.getTelephone());
        vo.setStatus(msg.getStatus());

        vo = dbf.persistAndRefresh(vo);

        APICreateNodeEvent evt = new APICreateNodeEvent(msg.getId());
        evt.setInventory(NodeInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateNodeMsg msg){
        NodeVO vo = dbf.findByUuid(msg.getTargetUuid(),NodeVO.class);
        boolean update = false;
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getCode() != null){
            vo.setCode(msg.getCode());
            update = true;
        }
        if(msg.getProperty() != null){
            vo.setProperty(msg.getProperty());
            update = true;
        }
        if(msg.getStatus() != null){
            vo.setStatus(msg.getStatus());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateNodeEvent evt = new APIUpdateNodeEvent(msg.getId());
        evt.setInventory(NodeInventory.valueOf(vo));
        bus.publish(evt);
    }

    //-----------------------------------------------------HANDLE-ENDPOINT----------------------------------------------
    private void handle(APICreateEndpointMsg msg){
        EndpointVO vo = new EndpointVO();

        vo.setUuid(Platform.getUuid());
        vo.setNodeUuid(msg.getNodeUuid());
        vo.setName(msg.getName());
        vo.setCode(msg.getCode());

        vo = dbf.persistAndRefresh(vo);

        APICreateEndpointEvent evt = new APICreateEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateEndpointMsg msg){
        EndpointVO vo = dbf.findByUuid(msg.getTargetUuid(),EndpointVO.class);
        boolean update = false;
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getCode() != null){
            vo.setCode(msg.getCode());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateEndpointEvent evt = new APIUpdateEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDisableEndpointMsg msg){
        EndpointVO vo = dbf.findByUuid(msg.getTargetUuid(),EndpointVO.class);
        vo.setEnabled(0);
        APIDisableEndpointEvent evt = new APIDisableEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIEnableEndpointMsg msg){
        EndpointVO vo = dbf.findByUuid(msg.getTargetUuid(),EndpointVO.class);
        vo.setEnabled(1);
        APIEnableEndpointEvent evt = new APIEnableEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICloseEndpointMsg msg){
        EndpointVO vo = dbf.findByUuid(msg.getTargetUuid(),EndpointVO.class);
        vo.setOpenToCustomers(0);
        APICloseEndpointEvent evt = new APICloseEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIOpenEndpointMsg msg){
        EndpointVO vo = dbf.findByUuid(msg.getTargetUuid(),EndpointVO.class);
        vo.setOpenToCustomers(1);
        APIOpenEndpointEvent evt = new APIOpenEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    //-----------------------------------------------------HANDLE-SWITCH------------------------------------------------
    private void handle(APICreateSwitchMsg msg){
        SwitchVO vo = new SwitchVO();

        vo.setUuid(Platform.getUuid());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setBrand(msg.getBrand());
        vo.setSwitchModelUuid(msg.getSwitchModelUuid());
        vo.setUpperType(msg.getUpperType());
        vo.setOwner(msg.getOwner());
        vo.setRack(msg.getRack());
        vo.setmIP(msg.getmIP());
        vo.setUsername(msg.getUsername());
        vo.setPassword(msg.getPassword());
        vo.setIsPrivate(msg.getIsPrivate());

        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchEvent evt = new APICreateSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateSwitchMsg msg){
        SwitchVO vo = dbf.findByUuid(msg.getTargetUuid(),SwitchVO.class);
        boolean update = false;
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getCode() != null){
            vo.setCode(msg.getCode());
            update = true;
        }
        if(msg.getBrand() != null){
            vo.setBrand(msg.getBrand());
            update = true;
        }
        if(msg.getUpperType() != null){
            vo.setUpperType(msg.getUpperType());
            update = true;
        }
        if(msg.getSwitchModelUuid() != null){
            vo.setSwitchModelUuid(msg.getSwitchModelUuid());
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
        if(msg.getUsername() != null){
            vo.setUsername(msg.getUsername());
            update = true;
        }
        if(msg.getPassword() != null){
            vo.setPassword(msg.getPassword());
            update = true;
        }
        if(msg.getOwner() != null){
            vo.setOwner(msg.getOwner());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateSwitchEvent evt = new APIUpdateSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIEnableSwitchMsg msg){
        SwitchVO vo = dbf.findByUuid(msg.getTargetUuid(),SwitchVO.class);
        vo.setEnabled(1);
        APIEnableSwitchEvent evt = new APIEnableSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDisableSwitchMsg msg){
        SwitchVO vo = dbf.findByUuid(msg.getTargetUuid(),SwitchVO.class);
        vo.setEnabled(0);
        APIDisableSwitchEvent evt = new APIDisableSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIPrivateSwitchMsg msg){
        SwitchVO vo = dbf.findByUuid(msg.getTargetUuid(),SwitchVO.class);
        boolean update = false;

        if(msg.getIsPrivate() != null){
            vo.setIsPrivate(msg.getIsPrivate());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIPrivateSwitchEvent evt = new APIPrivateSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
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
        if(msg instanceof APICreateNodeMsg){                //---------intercept-NODE-----------------------------------
            validate((APICreateNodeMsg) msg);
        }else if(msg instanceof APIUpdateNodeMsg){
            validate((APIUpdateNodeMsg) msg);
        }else if(msg instanceof APICreateEndpointMsg){      //---------intercept-ENDPOINT-------------------------------
            validate((APICreateEndpointMsg) msg);
        }else if(msg instanceof APIUpdateEndpointMsg){
            validate((APIUpdateEndpointMsg) msg);
        }else if(msg instanceof APIDisableEndpointMsg){
            validate((APIDisableEndpointMsg) msg);
        }else if(msg instanceof APIEnableEndpointMsg){
            validate((APIEnableEndpointMsg) msg);
        }else if(msg instanceof APICloseEndpointMsg){
            validate((APICloseEndpointMsg) msg);
        }else if(msg instanceof APIOpenEndpointMsg){
            validate((APIOpenEndpointMsg) msg);
        }else if(msg instanceof APICreateSwitchMsg){        //---------intercept-SWITCH---------------------------------
            validate((APICreateSwitchMsg) msg);
        }else if(msg instanceof APIUpdateSwitchMsg){
            validate((APIUpdateSwitchMsg) msg);
        }else if(msg instanceof APIEnableSwitchMsg){
            validate((APIEnableSwitchMsg) msg);
        }else if(msg instanceof APIDisableSwitchMsg){
            validate((APIDisableSwitchMsg) msg);
        }else if(msg instanceof APIPrivateSwitchMsg){
            validate((APIPrivateSwitchMsg) msg);
        }
        return msg;
    }

    //-----------------------------------------------------VALIDATE-NODE------------------------------------------------
    private void validate(APICreateNodeMsg msg){
        //判断code，name是否已经存在
        SimpleQuery<NodeVO> q = dbf.createQuery(NodeVO.class);
        SimpleQuery<NodeVO> q2 = dbf.createQuery(NodeVO.class);
        q.add(NodeVO_.code, Op.EQ, msg.getCode());
        q2.add(NodeVO_.name, Op.EQ, msg.getName());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("node's code %s is already exist ",msg.getCode()));
        }else if(q2.isExists()){
            throw new ApiMessageInterceptionException(argerr("node's name %s is already exist ",msg.getName()));
        }
    }

    private void validate(APIUpdateNodeMsg msg){
        //判断所修改的节点是否存在
        SimpleQuery<NodeVO> q = dbf.createQuery(NodeVO.class);
        q.add(NodeVO_.uuid, Op.EQ, msg.getTargetUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("node %s is not exist ",msg.getTargetUuid()));
        }
        //判断code，name是否已经存在
        if(msg.getCode() != null){
            SimpleQuery<NodeVO> q2 = dbf.createQuery(NodeVO.class);
            q2.add(NodeVO_.code, Op.EQ, msg.getCode());
            q2.add(NodeVO_.uuid, Op.NOT_EQ, msg.getTargetUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("node's code %s is already exist ",msg.getCode()));
            }
        }
        if(msg.getName() != null){
            SimpleQuery<NodeVO> q3 = dbf.createQuery(NodeVO.class);
            q3.add(NodeVO_.name, Op.EQ, msg.getName());
            q3.add(NodeVO_.uuid, Op.NOT_EQ, msg.getTargetUuid());
            if(q3.isExists()){
                throw new ApiMessageInterceptionException(argerr("node's name %s is already exist ",msg.getName()));
            }
        }
    }

    //-----------------------------------------------------VALIDATE-ENDPOINT--------------------------------------------
    private void validate(APICreateEndpointMsg msg){
        //判断code，name是否已经存在
        SimpleQuery<EndpointVO> q = dbf.createQuery(EndpointVO.class);
        SimpleQuery<EndpointVO> q2 = dbf.createQuery(EndpointVO.class);
        q.add(EndpointVO_.code, Op.EQ, msg.getCode());
        q2.add(EndpointVO_.name, Op.EQ, msg.getName());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("endpoint's code %s is already exist ",msg.getCode()));
        }else if(q2.isExists()){
            throw new ApiMessageInterceptionException(argerr("endpoint's name %s is already exist ",msg.getName()));
        }
        //判断连接点所属节点是否存在
        SimpleQuery<NodeVO> q3 = dbf.createQuery(NodeVO.class);
        q3.add(NodeVO_.uuid, Op.EQ, msg.getNodeUuid());
        if (!q3.isExists()) {
            throw new ApiMessageInterceptionException(argerr("node %s is not exist ",msg.getNodeUuid()));
        }
    }

    private void validate(APIUpdateEndpointMsg msg){
        //判断所修改的连接点是否存在
        SimpleQuery<EndpointVO> q = dbf.createQuery(EndpointVO.class);
        q.add(EndpointVO_.uuid, Op.EQ, msg.getTargetUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("endpoint %s is not exist ",msg.getTargetUuid()));
        }
        //判断code，name是否已经存在
        if(msg.getCode() != null){
            SimpleQuery<EndpointVO> q2 = dbf.createQuery(EndpointVO.class);
            q2.add(EndpointVO_.code, Op.EQ, msg.getCode());
            q2.add(EndpointVO_.uuid, Op.NOT_EQ, msg.getTargetUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("endpoint's code %s is already exist ",msg.getCode()));
            }
        }
        if(msg.getName() != null){
            SimpleQuery<EndpointVO> q3 = dbf.createQuery(EndpointVO.class);
            q3.add(EndpointVO_.name, Op.EQ, msg.getName());
            q3.add(EndpointVO_.uuid, Op.NOT_EQ, msg.getTargetUuid());
            if(q3.isExists()){
                throw new ApiMessageInterceptionException(argerr("endpoint's name %s is already exist ",msg.getName()));
            }
        }

    }

    private void validate(APIDisableEndpointMsg msg){
        //判断所操作的连接点是否存在
        SimpleQuery<EndpointVO> q = dbf.createQuery(EndpointVO.class);
        q.add(EndpointVO_.uuid, Op.EQ, msg.getTargetUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("endpoint %s is not exist ",msg.getTargetUuid()));
        }
    }

    private void validate(APIEnableEndpointMsg msg){
        //判断所操作的连接点是否存在
        SimpleQuery<EndpointVO> q = dbf.createQuery(EndpointVO.class);
        q.add(EndpointVO_.uuid, Op.EQ, msg.getTargetUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("endpoint %s is not exist ",msg.getTargetUuid()));
        }
    }

    private void validate(APICloseEndpointMsg msg){
        //判断所操作的连接点是否存在
        SimpleQuery<EndpointVO> q = dbf.createQuery(EndpointVO.class);
        q.add(EndpointVO_.uuid, Op.EQ, msg.getTargetUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("endpoint %s is not exist ",msg.getTargetUuid()));
        }
    }

    private void validate(APIOpenEndpointMsg msg){
        //判断所操作的连接点是否存在
        SimpleQuery<EndpointVO> q = dbf.createQuery(EndpointVO.class);
        q.add(EndpointVO_.uuid, Op.EQ, msg.getTargetUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("endpoint %s is not exist ",msg.getTargetUuid()));
        }
    }

    //-----------------------------------------------------VALIDATE-SWITCH----------------------------------------------
    private void validate(APICreateSwitchMsg msg){
        //判断code，name是否已经存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        SimpleQuery<SwitchVO> q2 = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.code, Op.EQ, msg.getCode());
        q2.add(SwitchVO_.name, Op.EQ, msg.getName());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("switch's code %s is already exist ",msg.getCode()));
        }else if(q2.isExists()){
            throw new ApiMessageInterceptionException(argerr("switch's name %s is already exist ",msg.getName()));
        }
        //判断交换机所属连接点是否存在
        SimpleQuery<EndpointVO> q3 = dbf.createQuery(EndpointVO.class);
        q3.add(EndpointVO_.uuid, Op.EQ, msg.getEndpointUuid());
        if (!q3.isExists()) {
            throw new ApiMessageInterceptionException(argerr("endpoint %s is not exist ",msg.getEndpointUuid()));
        }
        //判断交换机所选的交换机型号是否存在

    }

    private void validate(APIUpdateSwitchMsg msg){
        //判断所修改的交换机是否存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.uuid, Op.EQ, msg.getTargetUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switch %s is not exist ",msg.getTargetUuid()));
        }
        //判断code，name是否已经存在
        if(msg.getCode() != null){
            SimpleQuery<SwitchVO> q2 = dbf.createQuery(SwitchVO.class);
            q2.add(SwitchVO_.code, Op.EQ, msg.getCode());
            q2.add(SwitchVO_.uuid, Op.NOT_EQ, msg.getTargetUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("switch's code %s is already exist ",msg.getCode()));
            }
        }
        if(msg.getName() != null){
            SimpleQuery<SwitchVO> q3 = dbf.createQuery(SwitchVO.class);
            q3.add(SwitchVO_.name, Op.EQ, msg.getName());
            q3.add(SwitchVO_.uuid, Op.NOT_EQ, msg.getTargetUuid());
            if(q3.isExists()){
                throw new ApiMessageInterceptionException(argerr("switch's name %s is already exist ",msg.getName()));
            }
        }
        //判断所修改的交换机型号是否存在


    }

    private void validate(APIEnableSwitchMsg msg){
        //判断所操作的交换机是否存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.uuid, Op.EQ, msg.getTargetUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switch %s is not exist ",msg.getTargetUuid()));
        }
    }

    private void validate(APIDisableSwitchMsg msg){
        //判断所操作的交换机是否存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.uuid, Op.EQ, msg.getTargetUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switch %s is not exist ",msg.getTargetUuid()));
        }
    }

    private void validate(APIPrivateSwitchMsg msg){
        //判断所操作的交换机是否存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.uuid, Op.EQ, msg.getTargetUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switch %s is not exist ",msg.getTargetUuid()));
        }
    }
}
