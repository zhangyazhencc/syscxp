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
import org.zstack.tunnel.header.endpoint.*;
import org.zstack.tunnel.header.node.*;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.TypedQuery;

import java.util.List;

import static org.zstack.core.Platform.argerr;

/**
 * Created by DCY on 2017-09-07
 */
public class NodeManagerImpl extends AbstractService implements NodeManager, ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(NodeManagerImpl.class);

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
        if (msg instanceof APICreateNodeMsg) {
            handle((APICreateNodeMsg) msg);
        } else if (msg instanceof APIUpdateNodeMsg) {
            handle((APIUpdateNodeMsg) msg);
        } else if (msg instanceof APIDeleteNodeMsg) {
            handle((APIDeleteNodeMsg) msg);
        } else if (msg instanceof APIGetDistinctNodeCityMsg) {
            handle((APIGetDistinctNodeCityMsg) msg);
        } else if (msg instanceof APICreateEndpointMsg) {
            handle((APICreateEndpointMsg) msg);
        } else if (msg instanceof APIUpdateEndpointMsg) {
            handle((APIUpdateEndpointMsg) msg);
        } else if (msg instanceof APIDeleteEndpointMsg) {
            handle((APIDeleteEndpointMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateNodeMsg msg) {
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
        if (msg.getExtensionInfoUuid() != null) {
            vo.setExtensionInfoUuid(msg.getExtensionInfoUuid());
        } else {
            vo.setExtensionInfoUuid(null);
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
        } else {
            vo.setDescription(null);
        }

        vo = dbf.persistAndRefresh(vo);

        APICreateNodeEvent evt = new APICreateNodeEvent(msg.getId());
        evt.setInventory(NodeInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateNodeMsg msg) {
        NodeVO vo = dbf.findByUuid(msg.getUuid(), NodeVO.class);

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
        if (msg.getExtensionInfoUuid() != null) {
            vo.setExtensionInfoUuid(msg.getExtensionInfoUuid());
        } else {
            vo.setExtensionInfoUuid(null);
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
        } else {
            vo.setDescription(null);
        }

        vo = dbf.updateAndRefresh(vo);

        APIUpdateNodeEvent evt = new APIUpdateNodeEvent(msg.getId());
        evt.setInventory(NodeInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteNodeMsg msg) {
        String uuid = msg.getUuid();
        NodeEO eo = dbf.findByUuid(uuid, NodeEO.class);

        if (eo != null) {
            eo.setDeleted(1);
            dbf.update(eo);
        }

        APIDeleteNodeEvent event = new APIDeleteNodeEvent(msg.getId());
        bus.publish(event);
    }

    private void handle(APIGetDistinctNodeCityMsg msg) {
        String sql = "select distinct city from NodeVO";
        TypedQuery<String> q = dbf.getEntityManager().createQuery(sql,String.class);
        List<String> cities = q.getResultList();

        APIGetDistinctNodeCityReply reply = new APIGetDistinctNodeCityReply();
        reply.setCities(cities);
        bus.reply(msg,reply);
    }

    private void handle(APICreateEndpointMsg msg) {
        EndpointVO vo = new EndpointVO();

        vo.setUuid(Platform.getUuid());
        vo.setNodeUuid(msg.getNodeUuid());
        vo.setName(msg.getName());
        vo.setCode(msg.getCode());
        vo.setEndpointType(msg.getEndpointType());
        vo.setEnabled(1);
        vo.setOpenToCustomers(0);
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
        } else {
            vo.setDescription(null);
        }

        vo = dbf.persistAndRefresh(vo);

        APICreateEndpointEvent evt = new APICreateEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateEndpointMsg msg) {
        EndpointVO vo = dbf.findByUuid(msg.getUuid(), EndpointVO.class);

        vo.setName(msg.getName());
        vo.setCode(msg.getCode());
        vo.setEnabled(msg.getEnabled());
        vo.setOpenToCustomers(msg.getOpenToCustomers());

        vo = dbf.updateAndRefresh(vo);

        APIUpdateEndpointEvent evt = new APIUpdateEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteEndpointMsg msg) {
        String uuid = msg.getUuid();
        EndpointEO eo = dbf.findByUuid(uuid, EndpointEO.class);
        if (eo != null) {
            eo.setDeleted(1);
            dbf.update(eo);
        }

        APIDeleteNodeEvent event = new APIDeleteNodeEvent(msg.getId());
        // NodeInventory inventory = NodeInventory.valueOf(eo);
        // event.setInventory(inventory);
        bus.publish(event);
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
        return bus.makeLocalServiceId(NodeConstant.SERVICE_ID);
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateNodeMsg) {
            validate((APICreateNodeMsg) msg);
        } else if (msg instanceof APIUpdateNodeMsg) {
            validate((APIUpdateNodeMsg) msg);
        } else if (msg instanceof APICreateEndpointMsg) {
            validate((APICreateEndpointMsg) msg);
        } else if (msg instanceof APIUpdateEndpointMsg) {
            validate((APIUpdateEndpointMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateNodeMsg msg) {
        //判断code是否已经存在
        SimpleQuery<NodeVO> q = dbf.createQuery(NodeVO.class);
        q.add(NodeVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("node's code %s is already exist ", msg.getCode()));
        }
    }

    private void validate(APIUpdateNodeMsg msg) {
        //判断所修改的节点是否存在
        SimpleQuery<NodeVO> q = dbf.createQuery(NodeVO.class);
        q.add(NodeVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("node %s is not exist ", msg.getUuid()));
        }
        //判断code是否已经存在
        if (msg.getCode() != null) {
            SimpleQuery<NodeVO> q2 = dbf.createQuery(NodeVO.class);
            q2.add(NodeVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q2.add(NodeVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q2.isExists()) {
                throw new ApiMessageInterceptionException(argerr("node's code %s is already exist ", msg.getCode()));
            }
        }

    }

    private void validate(APICreateEndpointMsg msg) {
        //判断code是否已经存在
        SimpleQuery<EndpointVO> q = dbf.createQuery(EndpointVO.class);
        q.add(EndpointVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("endpoint's code %s is already exist ", msg.getCode()));
        }
        //判断连接点所属节点是否存在
        SimpleQuery<NodeVO> q2 = dbf.createQuery(NodeVO.class);
        q2.add(NodeVO_.uuid, SimpleQuery.Op.EQ, msg.getNodeUuid());
        if (!q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("node %s is not exist ", msg.getNodeUuid()));
        }
    }

    private void validate(APIUpdateEndpointMsg msg) {
        //判断所修改的连接点是否存在
        SimpleQuery<EndpointVO> q = dbf.createQuery(EndpointVO.class);
        q.add(EndpointVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("endpoint %s is not exist ", msg.getUuid()));
        }
        //判断code是否已经存在
        if (msg.getCode() != null) {
            SimpleQuery<EndpointVO> q2 = dbf.createQuery(EndpointVO.class);
            q2.add(EndpointVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q2.add(EndpointVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q2.isExists()) {
                throw new ApiMessageInterceptionException(argerr("endpoint's code %s is already exist ", msg.getCode()));
            }
        }

    }
}
