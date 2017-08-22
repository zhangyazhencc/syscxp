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
import org.zstack.core.db.*;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.tunnel.header.identity.node.*;
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
        if(msg instanceof ApiCreateNodeMsg){
            handle((ApiCreateNodeMsg) msg);
        }else if(msg instanceof ApiUpdateNodeMsg){
            handle((ApiUpdateNodeMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(ApiCreateNodeMsg msg){
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

        ApiCreateNodeEvent evt = new ApiCreateNodeEvent(msg.getId());
        evt.setInventory(NodeInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(ApiUpdateNodeMsg msg){
        NodeVO vo = dbf.findByUuid(msg.getTargetUuid(),NodeVO.class);

        vo.setName(msg.getName());
        vo.setCode(msg.getCode());
        vo.setProperty(msg.getProperty());
        vo.setStatus(msg.getStatus());

        vo = dbf.updateAndRefresh(vo);

        ApiUpdateNodeEvent evt = new ApiUpdateNodeEvent(msg.getId());
        evt.setInventory(NodeInventory.valueOf(vo));
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
        if(msg instanceof ApiCreateNodeMsg){
            validate((ApiCreateNodeMsg) msg);
        }else if(msg instanceof ApiUpdateNodeMsg){
            validate((ApiUpdateNodeMsg) msg);
        }
        return msg;
    }

    private void validate(ApiCreateNodeMsg msg){
        SimpleQuery<NodeVO> q = dbf.createQuery(NodeVO.class);
        q.add(NodeVO_.name, Op.EQ, msg.getName());
        SimpleQuery<NodeVO> q2 = dbf.createQuery(NodeVO.class);
        q2.add(NodeVO_.code, Op.EQ, msg.getCode());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("unable to create a node. A node name called %s is already ",msg.getName()));
        }else if(q2.isExists()){
            throw new ApiMessageInterceptionException(argerr("unable to create a node. A node code called %s is already ",msg.getCode()));
        }
    }

    private void validate(ApiUpdateNodeMsg msg){
        SimpleQuery<NodeVO> q = dbf.createQuery(NodeVO.class);
        q.add(NodeVO_.name, Op.EQ, msg.getName());
        SimpleQuery<NodeVO> q2 = dbf.createQuery(NodeVO.class);
        q2.add(NodeVO_.code, Op.EQ, msg.getCode());

        NodeVO vo = dbf.findByUuid(msg.getTargetUuid(),NodeVO.class);
        String oldName = vo.getName();
        String oldCode = vo.getCode();
        if (!msg.getName().equals(oldName) && q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("unable to update a node. A node name called %s is already ",msg.getName()));
        }else if(!msg.getCode().equals(oldCode) && q2.isExists()){
            throw new ApiMessageInterceptionException(argerr("unable to update a node. A node code called %s is already ",msg.getCode()));
        }
    }
}
