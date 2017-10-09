package com.syscxp.tunnel.manage;

import com.syscxp.tunnel.header.endpoint.*;
import com.syscxp.tunnel.header.node.*;
import com.syscxp.tunnel.header.switchs.PhysicalSwitchVO;
import com.syscxp.tunnel.header.switchs.SwitchVO;
import com.syscxp.tunnel.header.switchs.SwitchVO_;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.EventFacade;
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
import com.syscxp.tunnel.header.endpoint.*;
import com.syscxp.tunnel.header.node.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.persistence.TypedQuery;

import java.util.List;

import static com.syscxp.core.Platform.argerr;

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

    @Autowired
    private MongoTemplate mongoTemplate;


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
        } else if (msg instanceof APICreateNodeExtensionInfoMsg) {
            handle((APICreateNodeExtensionInfoMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateNodeExtensionInfoMsg msg) {



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
        NodeVO vo = dbf.findByUuid(uuid,NodeVO.class);

        NodeEO eo = dbf.findByUuid(uuid, NodeEO.class);
        eo.setDeleted(1);
        dbf.update(eo);

        APIDeleteNodeEvent event = new APIDeleteNodeEvent(msg.getId());
        NodeInventory inventory = NodeInventory.valueOf(vo);
        event.setInventory(inventory);

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
        EndpointVO vo = dbf.findByUuid(uuid,EndpointVO.class);

        EndpointEO eo = dbf.findByUuid(uuid, EndpointEO.class);
        eo.setDeleted(1);
        dbf.update(eo);

        APIDeleteEndpointEvent event = new APIDeleteEndpointEvent(msg.getId());
        EndpointInventory inventory = EndpointInventory.valueOf(vo);
        event.setInventory(inventory);

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
        } else if (msg instanceof APIDeleteNodeMsg) {
            validate((APIDeleteNodeMsg) msg);
        } else if (msg instanceof APICreateEndpointMsg) {
            validate((APICreateEndpointMsg) msg);
        } else if (msg instanceof APIUpdateEndpointMsg) {
            validate((APIUpdateEndpointMsg) msg);
        } else if (msg instanceof APIDeleteEndpointMsg) {
            validate((APIDeleteEndpointMsg) msg);
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

        //检查经纬度
        validateLongitudeAndLatitude(msg.getLatitude());
        validateLongitudeAndLatitude(msg.getLongtitude());
    }

    private void validate(APIUpdateNodeMsg msg) {
        //判断code是否已经存在
        if (msg.getCode() != null) {
            SimpleQuery<NodeVO> q = dbf.createQuery(NodeVO.class);
            q.add(NodeVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q.add(NodeVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("node's code %s is already exist ", msg.getCode()));
            }
        }

        //检查经纬度
        validateLongitudeAndLatitude(msg.getLatitude());
        validateLongitudeAndLatitude(msg.getLongtitude());
    }

    private void validate(APIDeleteNodeMsg msg) {
        //判断是否被连接点关联
        SimpleQuery<EndpointVO> queryEndpoint = dbf.createQuery(EndpointVO.class);
        queryEndpoint.add(EndpointVO_.nodeUuid,SimpleQuery.Op.EQ,msg.getUuid());
        if (queryEndpoint.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Endpoint exist,cannot be deleted!"));
        }

        //判断是否被物理交换机关联
        SimpleQuery<PhysicalSwitchVO> queryPhysicalSwitch = dbf.createQuery(PhysicalSwitchVO.class);
        queryPhysicalSwitch.add(EndpointVO_.nodeUuid,SimpleQuery.Op.EQ,msg.getUuid());
        if (queryPhysicalSwitch.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Physical switch exist,cannot be deleted!"));
        }
    }

    private void validate(APICreateEndpointMsg msg) {
        //判断code是否已经存在
        SimpleQuery<EndpointVO> q = dbf.createQuery(EndpointVO.class);
        q.add(EndpointVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("endpoint's code %s is already exist ", msg.getCode()));
        }
    }

    private void validate(APIUpdateEndpointMsg msg) {
        //判断code是否已经存在
        if (msg.getCode() != null) {
            SimpleQuery<EndpointVO> q = dbf.createQuery(EndpointVO.class);
            q.add(EndpointVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q.add(EndpointVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("endpoint's code %s is already exist ", msg.getCode()));
            }
        }
    }

    private void validate(APIDeleteEndpointMsg msg) {
        //判断是否被虚拟交换机关联
        SimpleQuery<SwitchVO> querySwitch = dbf.createQuery(SwitchVO.class);
        querySwitch.add(SwitchVO_.endpointUuid,SimpleQuery.Op.EQ,msg.getUuid());
        if (querySwitch.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Virtual switch exist,cannot be deleted!"));
        }
    }

    /**
     * 检查经度与维度是否合法
     * @param data：经度或维度值
     */
    private void validateLongitudeAndLatitude(Double data){
        if(data == null)
            throw new ApiMessageInterceptionException(argerr("longitude or latitude cannot be null!",""));
        if(data > 9999.999999 || data < -9999.999999)
            throw new ApiMessageInterceptionException(argerr("longitude or latitude ( %s ) must between -99999.99999 and 99999.99999!",data.toString()));
    }
}
