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
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

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
        }else if(msg instanceof APICreateSwitchAttributionMsg){
            handle((APICreateSwitchAttributionMsg) msg);
        }else if(msg instanceof APIUpdateSwitchAttributionMsg){
            handle((APIUpdateSwitchAttributionMsg) msg);
        }else if(msg instanceof APICreateSwitchMsg){
            handle((APICreateSwitchMsg) msg);
        }else if(msg instanceof APIUpdateSwitchMsg){
            handle((APIUpdateSwitchMsg) msg);
        }else if(msg instanceof APICreateSwitchPortMsg){
            handle((APICreateSwitchPortMsg) msg);
        }else if(msg instanceof APICreateSwitchVlanMsg){
            handle((APICreateSwitchVlanMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateSwitchModelMsg msg){
        SwitchModelVO vo = new SwitchModelVO();

        vo.setUuid(Platform.getUuid());
        vo.setModel(msg.getModel());
        if(msg.getSubModel() != null){
            vo.setSubModel(msg.getSubModel());
        }else{
            vo.setSubModel(null);
        }
        vo.setMpls(msg.getMpls());

        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchModelEvent evt = new APICreateSwitchModelEvent(msg.getId());
        evt.setInventory(SwitchModelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateSwitchAttributionMsg msg){
        SwitchAttributionVO vo = new SwitchAttributionVO();

        vo.setUuid(Platform.getUuid());
        vo.setNodeUuid(msg.getNodeUuid());
        vo.setSwitchModelUuid(msg.getSwitchModelUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setBrand(msg.getBrand());
        vo.setOwner(msg.getOwner());
        vo.setRack(msg.getRack());
        vo.setmIP(msg.getmIP());
        vo.setUsername(msg.getUsername());
        vo.setPassword(msg.getPassword());
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }else{
            vo.setDescription(null);
        }

        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchAttributionEvent evt = new APICreateSwitchAttributionEvent(msg.getId());
        evt.setInventory(SwitchAttributionInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateSwitchAttributionMsg msg){
        SwitchAttributionVO vo = dbf.findByUuid(msg.getUuid(),SwitchAttributionVO.class);
        boolean update = false;

        if(msg.getNodeUuid() != null){
            vo.setNodeUuid(msg.getNodeUuid());
            update = true;
        }
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
        if(msg.getBrand() != null){
            vo.setBrand(msg.getBrand());
            update = true;
        }
        if(msg.getOwner() != null){
            vo.setOwner(msg.getOwner());
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
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateSwitchAttributionEvent evt = new APIUpdateSwitchAttributionEvent(msg.getId());
        evt.setInventory(SwitchAttributionInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateSwitchMsg msg){
        SwitchVO vo = new SwitchVO();

        vo.setUuid(Platform.getUuid());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setSwitchAttributionUuid(msg.getSwitchAttributionUuid());
        vo.setUpperType(msg.getUpperType());
        vo.setStatus(SwitchStatus.NORMAL);
        vo.setIsPrivate(msg.getIsPrivate());
        vo.setEnabled(1);
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }else{
            vo.setDescription(null);
        }

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
        if(msg.getSwitchAttributionUuid() != null){
            vo.setSwitchAttributionUuid(msg.getSwitchAttributionUuid());
            update = true;
        }
        if(msg.getUpperType() != null){
            vo.setUpperType(msg.getUpperType());
            update = true;
        }
        if(msg.getEnabled() != null){
            vo.setEnabled(msg.getEnabled());
            update = true;
        }
        if(msg.getStatus() != null){
            vo.setStatus(msg.getStatus());
            update = true;
        }
        if(msg.getIsPrivate() != null){
            vo.setIsPrivate(msg.getIsPrivate());
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

    private void handle(APICreateSwitchPortMsg msg){
        SwitchPortVO vo = new SwitchPortVO();

        vo.setUuid(Platform.getUuid());
        vo.setSwitchUuid(msg.getSwitchUuid());
        vo.setPortNum(null);
        vo.setPortName(msg.getPortName());
        vo.setPortType(msg.getPortType());
        vo.setLabel(msg.getLabel());
        vo.setIsExclusive(msg.getIsExclusive());
        vo.setEnabled(1);
        vo.setReuse(msg.getReuse());

        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchPortEvent evt = new APICreateSwitchPortEvent(msg.getId());
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
        }else if(msg instanceof APICreateSwitchAttributionMsg){
            validate((APICreateSwitchAttributionMsg) msg);
        }else if(msg instanceof APIUpdateSwitchAttributionMsg){
            validate((APIUpdateSwitchAttributionMsg) msg);
        }else if(msg instanceof APICreateSwitchMsg){
            validate((APICreateSwitchMsg) msg);
        }else if(msg instanceof APIUpdateSwitchMsg){
            validate((APIUpdateSwitchMsg) msg);
        }else if(msg instanceof APICreateSwitchPortMsg){
            validate((APICreateSwitchPortMsg) msg);
        }else if(msg instanceof APICreateSwitchVlanMsg){
            validate((APICreateSwitchVlanMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateSwitchModelMsg msg){
        //判断model是否已经存在
        SimpleQuery<SwitchModelVO> q = dbf.createQuery(SwitchModelVO.class);
        q.add(SwitchModelVO_.model, SimpleQuery.Op.EQ, msg.getModel());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("switchModel %s is already exist ",msg.getModel()));
        }
    }

    private void validate(APICreateSwitchAttributionMsg msg){
        //判断code是否已经存在
        SimpleQuery<SwitchAttributionVO> q = dbf.createQuery(SwitchAttributionVO.class);
        q.add(SwitchAttributionVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("switchAttribution's code %s is already exist ",msg.getCode()));
        }
        //判断交换机所属节点是否存在
        SimpleQuery<NodeVO> q2 = dbf.createQuery(NodeVO.class);
        q2.add(NodeVO_.uuid, SimpleQuery.Op.EQ, msg.getNodeUuid());
        if (!q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("node %s is not exist ",msg.getNodeUuid()));
        }
        //判断交换机所属型号是否存在
        SimpleQuery<SwitchModelVO> q3 = dbf.createQuery(SwitchModelVO.class);
        q3.add(SwitchModelVO_.uuid, SimpleQuery.Op.EQ, msg.getSwitchModelUuid());
        if (!q3.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switchModel %s is not exist ",msg.getSwitchModelUuid()));
        }
    }

    private void validate(APIUpdateSwitchAttributionMsg msg){
        //判断所修改的交换机是否存在
        SimpleQuery<SwitchAttributionVO> q = dbf.createQuery(SwitchAttributionVO.class);
        q.add(SwitchAttributionVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switchAttribution %s is not exist ",msg.getUuid()));
        }
        //判断code是否已经存在
        if(msg.getCode() != null){
            SimpleQuery<SwitchAttributionVO> q2 = dbf.createQuery(SwitchAttributionVO.class);
            q2.add(SwitchAttributionVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q2.add(SwitchAttributionVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("switchAttribution's code %s is already exist ",msg.getCode()));
            }
        }
        //判断交换机所属节点是否存在
        if(msg.getNodeUuid() != null){
            SimpleQuery<NodeVO> q3 = dbf.createQuery(NodeVO.class);
            q3.add(NodeVO_.uuid, SimpleQuery.Op.EQ, msg.getNodeUuid());
            if (!q3.isExists()) {
                throw new ApiMessageInterceptionException(argerr("node %s is not exist ",msg.getNodeUuid()));
            }
        }
        //判断交换机所属型号是否存在
        if(msg.getSwitchModelUuid() != null){
            SimpleQuery<SwitchModelVO> q4 = dbf.createQuery(SwitchModelVO.class);
            q4.add(SwitchModelVO_.uuid, SimpleQuery.Op.EQ, msg.getSwitchModelUuid());
            if (!q4.isExists()) {
                throw new ApiMessageInterceptionException(argerr("switchModel %s is not exist ",msg.getSwitchModelUuid()));
            }
        }
    }

    private void validate(APICreateSwitchMsg msg){
        //判断code是否已经存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("switch's code %s is already exist ",msg.getCode()));
        }
        //判断交换机所属连接点是否存在
        SimpleQuery<EndpointVO> q2 = dbf.createQuery(EndpointVO.class);
        q2.add(EndpointVO_.uuid, SimpleQuery.Op.EQ, msg.getEndpointUuid());
        if (!q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("endpoint %s is not exist ",msg.getEndpointUuid()));
        }
        //判断交换机所属物理交换机是否存在
        SimpleQuery<SwitchAttributionVO> q3 = dbf.createQuery(SwitchAttributionVO.class);
        q3.add(SwitchAttributionVO_.uuid, SimpleQuery.Op.EQ, msg.getSwitchAttributionUuid());
        if (!q3.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switchAttribution %s is not exist ",msg.getSwitchAttributionUuid()));
        }

    }

    private void validate(APIUpdateSwitchMsg msg){
        //判断所修改的交换机是否存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switch %s is not exist ",msg.getUuid()));
        }
        //判断code是否已经存在
        if(msg.getCode() != null){
            SimpleQuery<SwitchVO> q2 = dbf.createQuery(SwitchVO.class);
            q2.add(SwitchVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q2.add(SwitchVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("switch's code %s is already exist ",msg.getCode()));
            }
        }

    }


    private void validate(APICreateSwitchPortMsg msg){
        //判断端口所在的交换机UUID是否存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.uuid, SimpleQuery.Op.EQ, msg.getSwitchUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switch %s is not exist ",msg.getSwitchUuid()));
        }
        //端口其他验证。。。。

    }


    private void validate(APICreateSwitchVlanMsg msg){
        //判断VLAN所在的交换机UUID是否存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.uuid, SimpleQuery.Op.EQ, msg.getSwitchUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switch %s is not exist ",msg.getSwitchUuid()));
        }
        //VLAN验证。。。。

    }
}
