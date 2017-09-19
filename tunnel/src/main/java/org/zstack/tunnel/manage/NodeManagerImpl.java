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
import org.zstack.tunnel.header.switchs.PhysicalSwitchVO;
import org.zstack.tunnel.header.switchs.SwitchVO;
import org.zstack.tunnel.header.switchs.SwitchVO_;
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
        eo.setDeleted(1);
        dbf.update(eo);

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
        eo.setDeleted(1);
        dbf.update(eo);

        APIDeleteNodeEvent event = new APIDeleteNodeEvent(msg.getId());
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
        validateLongitudeAndLatitude(msg.getLatitude(),msg.getCode());
        validateLongitudeAndLatitude(msg.getLongtitude(),msg.getCode());
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
        validateLongitudeAndLatitude(msg.getLatitude(),msg.getCode());
        validateLongitudeAndLatitude(msg.getLongtitude(),msg.getCode());
    }

    private void validate(APIDeleteNodeMsg msg) {
        //判断是否被连接点关联
        SimpleQuery<EndpointVO> queryEndpoint = dbf.createQuery(EndpointVO.class);
        queryEndpoint.add(EndpointVO_.nodeUuid,SimpleQuery.Op.EQ,msg.getUuid());
        if (queryEndpoint.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Endpoint exist,cannot be deleted!", msg.getUuid()));
        }

        //判断是否被物理交换机关联
        SimpleQuery<PhysicalSwitchVO> queryPhysicalSwitch = dbf.createQuery(PhysicalSwitchVO.class);
        queryPhysicalSwitch.add(EndpointVO_.nodeUuid,SimpleQuery.Op.EQ,msg.getUuid());
        if (queryPhysicalSwitch.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Physical switch exist,cannot be deleted!", msg.getUuid()));
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
            throw new ApiMessageInterceptionException(argerr("Virtual switch exist,cannot be deleted!", msg.getUuid()));
        }
    }

    /**
     * 检查经度与维度是否合法
     * @param data：经度或维度值
     * @param msgCode：Code
     */
    private void validateLongitudeAndLatitude(Double data,String msgCode){
        if(data == null)
            throw new ApiMessageInterceptionException(argerr("longitude or latitude cannot be null!",msgCode));
        if(data > 99999.99999 || data < -99999.99999)
            throw new ApiMessageInterceptionException(argerr("longitude or latitude must between -99999.99999 and 99999.99999!",msgCode));
    }
}
