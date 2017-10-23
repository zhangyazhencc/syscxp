package com.syscxp.tunnel.manage;

import com.mongodb.util.JSON;
import com.syscxp.tunnel.header.endpoint.*;
import com.syscxp.tunnel.header.node.*;
import com.syscxp.tunnel.header.switchs.PhysicalSwitchVO;
import com.syscxp.tunnel.header.switchs.SwitchVO;
import com.syscxp.tunnel.header.switchs.SwitchVO_;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.json.JSONObject;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import javax.persistence.TypedQuery;

import java.util.List;

import static com.syscxp.core.Platform.argerr;

import java.util.Map;
import java.util.Set;

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
        } else if (msg instanceof APIGetNodeExtensionInfoMsg) {
            handle((APIGetNodeExtensionInfoMsg) msg);
        } else if (msg instanceof APIDeleteNodeExtensionInfoMsg) {
            handle((APIDeleteNodeExtensionInfoMsg) msg);
        } else if (msg instanceof APIUpdateNodeExtensionInfoMsg) {
            handle((APIUpdateNodeExtensionInfoMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIUpdateNodeExtensionInfoMsg msg) {

        com.alibaba.fastjson.JSONObject newInfo = com.alibaba.fastjson.JSONObject.
                parseObject(msg.getNewNodeExtensionInfo());

        com.alibaba.fastjson.JSONObject oldInfo = mongoTemplate.findOne(new Query(Criteria.where("node_id").is(
                newInfo.getJSONObject("nodeExtensionInfo").get("node_id"))),com.alibaba.fastjson.JSONObject.class);


        Map<String,Object> oldmap = oldInfo;
        Map<String,Object> newmap = newInfo;
        Set<String> keySet = oldmap.keySet();
        for (String key : keySet) {
            if(newmap.containsKey(key)){
                if(oldmap.get(key) instanceof JSONObject){
                    Map<String,Object> oldmap1 = (Map)oldmap.get(key);
                    Map<String,Object> newmap1 = (Map)newmap.get(key);
                    Set<String> keySet1 = oldmap1.keySet();
                    for (String key1 : keySet1) {
                        if(newmap1.containsKey(key1)){
                            if(oldmap1.get(key1) instanceof JSONObject){
                                Map<String,Object> oldmap2 = (Map)oldmap1.get(key1);
                                Map<String,Object> newmap2 = (Map)newmap1.get(key1);
                                Set<String> keySet2 = oldmap2.keySet();
                                for (String key2 : keySet2) {
                                    if(newmap2.containsKey(key2)){
                                        if(oldmap2.get(key2) instanceof JSONObject){
                                            Map<String,Object> oldmap3 = (Map)oldmap2.get(key2);
                                            Map<String,Object> newmap3 = (Map)newmap2.get(key2);
                                            Set<String> keySet3 = oldmap3.keySet();
                                            for (String key3 : keySet3) {
                                                if(newmap3.containsKey(key3)){
                                                    if(oldmap3.get(key3) instanceof JSONObject){
                                                        System.out.println("");
                                                    }else{
                                                        oldmap3.put(key3, newmap3.get(key3));
                                                    }
                                                }
                                            }
                                            oldmap2.put(key2, oldmap3);
                                        }else{
                                            oldmap2.put(key2, newmap2.get(key2));
                                        }
                                    }
                                }
                                oldmap1.put(key1, oldmap2);
                            }else{
                                oldmap1.put(key1, newmap1.get(key1));
                            }
                        }
                    }
                    oldmap.put(key,oldmap1);
                }else{
                    oldmap.put(key,newmap.get(key));
                }

            }
        }




        APIUpdateNodeExtensionInfoEvent event =  new APIUpdateNodeExtensionInfoEvent(msg.getId());
        mongoTemplate.save(oldmap);
        event.setInventory(oldmap.toString());

        bus.publish(event);

    }

    private void handle(APIDeleteNodeExtensionInfoMsg msg) {
        mongoTemplate.remove(new Query(Criteria.where("node_id").is(msg.getNodeId())),NodeExtensionInfo.class);
        APIDeleteNodeExtensionInfoEvent event = new APIDeleteNodeExtensionInfoEvent(msg.getId());
        bus.publish(event);
    }

    private void handle(APIGetNodeExtensionInfoMsg msg) {


        APIGetNodeExtensionInfoReply reply = new APIGetNodeExtensionInfoReply();
        reply.setNodeExtensionInfo(JSONObjectUtil.toJsonString(
                mongoTemplate.findOne(new Query(Criteria.where("node_id").is(msg.getNodeId())),NodeExtensionInfo.class)
        ));
        bus.reply(msg,reply);
    }

    private void handle(APICreateNodeExtensionInfoMsg msg) {

        mongoTemplate.insert(JSONObjectUtil.toObject(msg.getNodeExtensionInfo(),NodeExtensionInfo.class));
        APICreateNodeExtensionInfoEvent event = new APICreateNodeExtensionInfoEvent(msg.getId());
        event.setInventory(msg.getNodeExtensionInfo());
        bus.publish(event);

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
        vo.setExtensionInfoUuid(msg.getExtensionInfoUuid());
        vo.setDescription(msg.getDescription());
        vo = dbf.persistAndRefresh(vo);

        APICreateNodeEvent evt = new APICreateNodeEvent(msg.getId());
        evt.setInventory(NodeInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateNodeMsg msg) {
        NodeVO vo = dbf.findByUuid(msg.getUuid(), NodeVO.class);
        boolean update = false;
        if(msg.getCode() != null){
            vo.setCode(msg.getCode());
            update = true;
        }
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getLongtitude() != null){
            vo.setLongtitude(msg.getLongtitude());
            update = true;
        }
        if(msg.getLatitude() != null){
            vo.setLatitude(msg.getLatitude());
            update = true;
        }
        if(msg.getProperty() != null){
            vo.setProperty(msg.getProperty());
            update = true;
        }
        if(msg.getProvince() != null){
            vo.setProvince(msg.getProvince());
            update = true;
        }
        if(msg.getCity() != null){
            vo.setCity(msg.getCity());
            update = true;
        }
        if(msg.getAddress() != null){
            vo.setAddress(msg.getAddress());
            update = true;
        }
        if(msg.getContact() != null){
            vo.setContact(msg.getContact());
            update = true;
        }
        if(msg.getTelephone() != null){
            vo.setTelephone(msg.getTelephone());
            update = true;
        }
        if(msg.getStatus() != null){
            vo.setStatus(msg.getStatus());
            update = true;
        }
        if (msg.getExtensionInfoUuid() != null) {
            vo.setExtensionInfoUuid(msg.getExtensionInfoUuid());
            update = true;
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateNodeEvent evt = new APIUpdateNodeEvent(msg.getId());
        evt.setInventory(NodeInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteNodeMsg msg) {
        String uuid = msg.getUuid();

        NodeVO vo = dbf.findByUuid(uuid,NodeVO.class);
        dbf.remove(vo);

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
        vo.setState(msg.getState());
        vo.setStatus(msg.getStatus());
        vo.setDescription(msg.getDescription());

        vo = dbf.persistAndRefresh(vo);

        APICreateEndpointEvent evt = new APICreateEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateEndpointMsg msg) {
        EndpointVO vo = dbf.findByUuid(msg.getUuid(), EndpointVO.class);
        boolean update = false;

        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getCode() != null){
            vo.setCode(msg.getCode());
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
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateEndpointEvent evt = new APIUpdateEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteEndpointMsg msg) {
        String uuid = msg.getUuid();

        EndpointVO vo = dbf.findByUuid(uuid,EndpointVO.class);
        dbf.remove(vo);

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
