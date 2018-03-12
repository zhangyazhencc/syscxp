package com.syscxp.tunnel.node;

import com.alibaba.fastjson.JSONObject;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.NodeConstant;
import com.syscxp.header.tunnel.endpoint.*;
import com.syscxp.header.tunnel.host.MonitorHostVO;
import com.syscxp.header.tunnel.host.MonitorHostVO_;
import com.syscxp.header.tunnel.node.*;
import com.syscxp.header.tunnel.switchs.PhysicalSwitchVO;
import com.syscxp.header.tunnel.switchs.SwitchVO;
import com.syscxp.header.tunnel.switchs.SwitchVO_;
import com.syscxp.header.tunnel.tunnel.TunnelSwitchPortVO;
import com.syscxp.header.tunnel.tunnel.TunnelSwitchPortVO_;
import com.syscxp.header.tunnel.tunnel.TunnelVO;
import com.syscxp.header.tunnel.tunnel.TunnelVO_;
import com.syscxp.utils.Digest;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private MongoTemplate mongoTemplate;
    @Autowired
    private RESTFacade restf;

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
        } else if (msg instanceof APIGetImageUploadInfoMsg) {
            handle((APIGetImageUploadInfoMsg) msg);
        }  else if (msg instanceof APIListNodeExtensionInfoMsg) {
            handle((APIListNodeExtensionInfoMsg) msg);
        }  else if (msg instanceof APICreateInnerEndpointMsg) {
            handle((APICreateInnerEndpointMsg) msg);
        }  else if (msg instanceof APIDeleteInnerEndpointMsg) {
            handle((APIDeleteInnerEndpointMsg) msg);
        } else if(msg instanceof APIListCountryNodeMsg ){
            handle((APIListCountryNodeMsg) msg);
        } else if(msg instanceof APIListProvinceNodeMsg){
            handle((APIListProvinceNodeMsg) msg);
        }else if(msg instanceof APIListCityNodeMsg){
            handle((APIListCityNodeMsg) msg);
        }else if(msg instanceof APIDeleteImageMsg){
            handle((APIDeleteImageMsg) msg);
        }else if(msg instanceof APIUploadImageUrlMsg){
            handle((APIUploadImageUrlMsg) msg);
        }else if(msg instanceof APIReconcileNodeExtensionInfoMsg){
            handle((APIReconcileNodeExtensionInfoMsg) msg);
        }else if(msg instanceof APIAttachNodeToZoneMsg){
            handle((APIAttachNodeToZoneMsg) msg);
        }else if(msg instanceof APIDetachNodeFromZoneMsg){
            handle((APIDetachNodeFromZoneMsg) msg);
        }
        else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIAttachNodeToZoneMsg msg){
        APIAttachNodeToZoneEvent event = new APIAttachNodeToZoneEvent(msg.getId());

        ZoneNodeRefVO refVO = Q.New(ZoneNodeRefVO.class).eq(ZoneNodeRefVO_.zoneUuid, msg.getZoneUuid())
                .eq(ZoneNodeRefVO_.nodeUuid, msg.getNodeUuid()).find();
        if (refVO == null){
            refVO = new ZoneNodeRefVO();
            refVO.setUuid(Platform.getUuid());
            refVO.setZoneUuid(msg.getZoneUuid());
            refVO.setNodeUuid(msg.getNodeUuid());

            dbf.persistAndRefresh(refVO);
            event.setInventory(ZoneNodeRefInventory.valueOf(refVO));
        }else{
            throw new ApiMessageInterceptionException(argerr("node is exist!"));
        }

        bus.publish(event);
    }
    private void handle(APIDetachNodeFromZoneMsg msg){
        APIDetachNodeFromZoneEvent event = new APIDetachNodeFromZoneEvent(msg.getId());
        UpdateQuery.New(ZoneNodeRefVO.class).eq(ZoneNodeRefVO_.zoneUuid, msg.getZoneUuid())
                .eq(ZoneNodeRefVO_.nodeUuid, msg.getNodeUuid()).delete();

        bus.publish(event);
    }

    private void handle(APIReconcileNodeExtensionInfoMsg msg) {

        APIReconcileNodeExtensionInfoEvent event = new APIReconcileNodeExtensionInfoEvent(msg.getId());

        Update date = new Update();

        if(msg.getStatus() != null){
            date.set("status", msg.getStatus());
        }
        if(msg.getProvince() != null){
            date.set("province", msg.getProvince());
        }
        if(msg.getProperty() != null){
            date.set("property", msg.getProperty());
        }
        if(msg.getRoomName() != null){
            date.set("machineRoomInfo.outer.roomName", msg.getRoomName());
        }
        if(msg.getRoomAddress() != null){
            date.set("machineRoomInfo.outer.roomAddress", msg.getRoomAddress());
        }
        if(msg.getConsignee() != null){
            date.set("roomNOC.inner.consignee", msg.getConsignee());
        }
        if(msg.getConsigneePhone() != null){
            date.set("roomNOC.inner.consigneePhone", msg.getConsigneePhone());
        }

        mongoTemplate.updateFirst(new Query(Criteria.where("node_id").is(msg.getNode_id())),
                date,NodeExtensionInfo.class);

        bus.publish(event);
    }

    private void handle(APIUploadImageUrlMsg msg) {
        APIUploadImageUrlEvent event = new APIUploadImageUrlEvent(msg.getId());

        NodeExtensionInfo node =  mongoTemplate.findOne(new Query(Criteria.where("node_id").
                is(msg.getNodeId())), NodeExtensionInfo.class);
        if(node != null){
            List<String> list = new ArrayList();
            if(node.getImages_url() != null){
                list= node.getImages_url();
                list.addAll(msg.getImage_urls());
            }else{
                list.addAll(msg.getImage_urls());
            }
            mongoTemplate.updateMulti(new Query(Criteria.where("node_id").is(msg.getNodeId())),
                    new Update().set("images_url", list),NodeExtensionInfo.class);
        }else{
            List<String> list = new ArrayList();
            list.addAll(msg.getImage_urls());
            mongoTemplate.upsert(new Query(Criteria.where("node_id").is(msg.getNodeId())),
                    new Update().set("images_url", list),NodeExtensionInfo.class);
        }

        bus.publish(event);
    }

    private void handle(APIDeleteImageMsg msg) {
        APIDeleteImageEvent event = new APIDeleteImageEvent(msg.getId());

        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String md5 = Digest.getMD5(msg.getNodeId() + timestamp + NodeImageGlobalProperty.UPLOAD_KEY + msg.getImage_url());

        StringBuffer prame = new StringBuffer();
        prame.append("node_id=");
        prame.append(msg.getNodeId());
        prame.append("&image_url=");
        prame.append(msg.getImage_url());
        prame.append("&timestamp=");
        prame.append(timestamp);
        prame.append("&md5=");
        prame.append(md5);


        Map rsp = restf.syncJsonPost(NodeImageGlobalProperty.DELETE_URL+"?"+
                prame.toString(), null,Map.class);

        if(rsp.get("success") != null && (boolean)rsp.get("success")){
            System.out.println("successfully");

            mongoTemplate.updateMulti(new Query(Criteria.where("node_id").is(msg.getNodeId())),
                    new Update().pull("images_url", msg.getImage_url()),NodeExtensionInfo.class);

        }else{
            event.setError(Platform.argerr("delete image fail"));
        }

        bus.publish(event);
    }

    private void handle(APIListCityNodeMsg msg) {
        List<String> cityNodeInventoryList = new ArrayList<>();

        String sql = "select distinct city from NodeVO where province = :province";
        TypedQuery<Tuple> tfq = dbf.getEntityManager().createQuery(sql, Tuple.class);
        tfq.setParameter("province", msg.getProvice());
        List<Tuple> ts = tfq.getResultList();
        for (Tuple t : ts) {
           cityNodeInventoryList.add(t.get(0, String.class));
        }
        APIListCityNodeReply reply = new APIListCityNodeReply();
        reply.setCitys(cityNodeInventoryList);
        bus.reply(msg,reply);

    }

    private void handle(APIListProvinceNodeMsg msg) {
        List<String> domesticNodeInventoryList;

        SimpleQuery<NodeVO> q = dbf.createQuery(NodeVO.class);
        if(msg.getCountry() != null){
            q.add(NodeVO_.country, SimpleQuery.Op.EQ, msg.getCountry());
        }
        q.select(NodeVO_.province);
        q.groupBy(NodeVO_.province);
        domesticNodeInventoryList = q.listValue();

        APIListProvinceNodeReply reply = new APIListProvinceNodeReply();
        reply.setProvinces(domesticNodeInventoryList);
        bus.reply(msg,reply);


    }

    private void handle(APIListCountryNodeMsg msg) {
        List<String> abroadNodeInventoryList = new ArrayList<>();

        String sql = "select distinct country from NodeVO";
        TypedQuery<Tuple> tfq = dbf.getEntityManager().createQuery(sql, Tuple.class);
        List<Tuple> ts = tfq.getResultList();
        for (Tuple t : ts) {
            abroadNodeInventoryList.add(t.get(0, String.class));
        }

        APIListCountryNodeReply reply = new APIListCountryNodeReply();
        reply.setCountrys(abroadNodeInventoryList);
        bus.reply(msg,reply);

    }

    private void handle(APIListNodeExtensionInfoMsg msg) {

        Query query = new Query();
        if(msg.getOperatorCategory() != null){
            query.addCriteria(Criteria.where("machineRoomInfo.outer.operatorCategory").is(msg.getOperatorCategory()));
        }
        if(msg.getProvince() != null){
            String[] citys = msg.getProvince().split(",");
            query.addCriteria(Criteria.where("province").in(Arrays.asList(msg.getProvince().split(","))));
        }
        if(msg.getRoomLevel() != null){
            query.addCriteria(Criteria.where("machineRoomInfo.outer.roomLevel").is(msg.getRoomLevel()));
        }
        if(msg.getProperty() != null){
            query.addCriteria(Criteria.where("property").all(msg.getProperty()));
        }else{
            query.addCriteria(Criteria.where("property").all("idc_node"));

        }

        query.addCriteria(Criteria.where("status").is("open"));

        Long count = mongoTemplate.count(query,"nodeExtensionInfo");


        if(msg.getOrderBy() != null && msg.getOrderPolicy() != null){
            query.with(new Sort(new Sort.Order("DESC".equals(
                    msg.getOrderPolicy().toUpperCase())?Sort.Direction.DESC:
                    Sort.Direction.ASC,msg.getOrderBy())));
        }

        if(msg.getPageNo() == null){
            msg.setPageNo("1");
        }
        if(msg.getPage_size() == null){
            msg.setPage_size("15");
        }

        query.skip(Integer.valueOf(msg.getPage_size())*(Integer.valueOf(msg.getPageNo())-1));
        query.limit(Integer.valueOf(msg.getPage_size()));
        Long total = mongoTemplate.count(query,"nodeExtensionInfo");

        List<NodeExtensionInfo> list = mongoTemplate.find(query,NodeExtensionInfo.class,"nodeExtensionInfo");
        NodeExtensionInfoList nodes = new NodeExtensionInfoList();
        nodes.setPage_no(msg.getPageNo());
        nodes.setCount(String.valueOf(count));
        Long pageCout = count/Integer.valueOf(msg.getPage_size()) + 1;
        List<Integer> PageRange = new ArrayList<>();
        for (int i = 0; i < pageCout; i++) {
            PageRange.add(i+1);
        }
        nodes.setPage_count(String.valueOf(pageCout));
        nodes.setPage_range(PageRange);
        nodes.setNodeExtensionInfos(JSONObjectUtil.toJsonString(list));
        nodes.setTotal(String.valueOf(total));
        nodes.setPage_size(msg.getPage_size());

        APIListNodeExtensionInfoReply reply = new APIListNodeExtensionInfoReply();
        reply.setNodeExtensionInfoList(nodes);
        bus.reply(msg,reply);
    }

    private void handle(APIGetImageUploadInfoMsg msg) {
        APIGetImageUploadInfoReply reply = new APIGetImageUploadInfoReply();

        reply.setUpload_url(NodeImageGlobalProperty.UPLOAD_URL);
        reply.setDelete_url(NodeImageGlobalProperty.DELETE_URL);
        reply.setImage_url_prefix(NodeImageGlobalProperty.IMAGE_URL_PREFIX);
        reply.setFileNumLimit(NodeImageGlobalProperty.FILENUMLIMIT);

        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String md5 = Digest.getMD5(msg.getNodeId() + timestamp + NodeImageGlobalProperty.UPLOAD_KEY);

        reply.setNodeId(msg.getNodeId());
        reply.setTimestamp(timestamp);
        reply.setMd5(md5);

        NodeExtensionInfo node = mongoTemplate.findOne(new Query(Criteria.where("node_id").is(msg.getNodeId())),
                NodeExtensionInfo.class,"nodeExtensionInfo");

        if(node != null && node.getImages_url() != null){
            reply.setImages_url(node.getImages_url());
        }

        bus.reply(msg,reply);
    }

    private void handle(APIUpdateNodeExtensionInfoMsg msg) {
        APIUpdateNodeExtensionInfoEvent event =  new APIUpdateNodeExtensionInfoEvent(msg.getId());

        JSONObject newInfo = JSONObject.parseObject(msg.getNewNodeExtensionInfo());
        NodeExtensionInfo node = mongoTemplate.findOne(new Query(Criteria.where("node_id").is(
                newInfo.getJSONObject("nodeExtensionInfo").get("node_id"))),NodeExtensionInfo.class,"nodeExtensionInfo");

        if(node == null){
            event.setError(Platform.argerr("this node Extension Information is not existent"));
        } else{
            String oldmogo = "{" +"\"nodeExtensionInfo\":" + JSONObjectUtil.toJsonString(node) +"}";

            JSONObject obj = new JSONObject();
            obj.putAll(JSONObject.parseObject(oldmogo));
            obj.putAll(newInfo);
            ((Map)obj.get("nodeExtensionInfo")).put("_id",node.get_id());

            mongoTemplate.save(obj.get("nodeExtensionInfo"),"nodeExtensionInfo");
            event.setInventory(obj.toString());
        }

        bus.publish(event);
    }

    private void handle(APIDeleteNodeExtensionInfoMsg msg) {
        mongoTemplate.remove(new Query(Criteria.where("node_id").is(msg.getUuid())),NodeExtensionInfo.class,"nodeExtensionInfo");
        APIDeleteNodeExtensionInfoEvent event = new APIDeleteNodeExtensionInfoEvent(msg.getId());
        bus.publish(event);
    }

    private void handle(APIGetNodeExtensionInfoMsg msg) {

        APIGetNodeExtensionInfoReply reply = new APIGetNodeExtensionInfoReply();
        reply.setNodeExtensionInfo(JSONObjectUtil.toJsonString(
                mongoTemplate.findOne(new Query(Criteria.where("node_id").is(msg.getNodeId())),NodeExtensionInfo.class,"nodeExtensionInfo")
        ));
        bus.reply(msg,reply);
    }

    private void handle(APICreateNodeExtensionInfoMsg msg) {

        APICreateNodeExtensionInfoEvent event = new APICreateNodeExtensionInfoEvent(msg.getId());
        com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(msg.getNodeExtensionInfo());

        String nodeId = json.getJSONObject("nodeExtensionInfo").getString("node_id").trim();
        if(nodeId != null){
            NodeExtensionInfo node = mongoTemplate.findOne(new Query(Criteria.where("node_id").is(nodeId)),
                    NodeExtensionInfo.class,"nodeExtensionInfo");
            if(node == null){
                mongoTemplate.insert(json.get("nodeExtensionInfo"),"nodeExtensionInfo");
            }else{
                event.setError(Platform.argerr("this node Extension Information has existed"));
            }

        }else{
            event.setError(Platform.argerr("jsonString is illegal"));
        }

        event.setInventory(msg.getNodeExtensionInfo());
        bus.publish(event);
    }

    private void handle(APICreateNodeMsg msg) {
        NodeVO vo = new NodeVO();

        vo.setUuid(Platform.getUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setLongitude(msg.getLongitude());
        vo.setLatitude(msg.getLatitude());
        vo.setProperty(msg.getProperty());
        vo.setCountry(msg.getCountry());
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
        if(msg.getLongitude() != null){
            vo.setLongitude(msg.getLongitude());
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
        if(msg.getCountry() != null){
            vo.setCountry(msg.getCountry());
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

    @Transactional
    private void handle(APIDeleteNodeMsg msg) {
        String uuid = msg.getUuid();

        NodeVO vo = dbf.findByUuid(uuid,NodeVO.class);
        dbf.remove(vo);

        mongoTemplate.remove(new Query(Criteria.where("node_id").is(uuid)),NodeExtensionInfo.class,"nodeExtensionInfo");

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
        if (msg.getEndpointType() == EndpointType.CLOUD)
            vo.setCloudType(msg.getCloudType());
        vo.setState(msg.getState());
        vo.setStatus(msg.getStatus());
        vo.setDescription(msg.getDescription());

        vo = dbf.persistAndRefresh(vo);

        APICreateEndpointEvent evt = new APICreateEndpointEvent(msg.getId());
        evt.setInventory(EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateInnerEndpointMsg msg) {
        InnerConnectedEndpointVO vo = new InnerConnectedEndpointVO();

        vo.setUuid(Platform.getUuid());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setConnectedEndpointUuid(msg.getConnectedEndpointUuid());
        vo.setName(msg.getName());

        vo = dbf.persistAndRefresh(vo);

        APICreateInnerEndpointEvent evt = new APICreateInnerEndpointEvent(msg.getId());
        evt.setInventory(InnerConnectedEndpointInventory.valueOf(vo));
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

    private void handle(APIDeleteInnerEndpointMsg msg) {
        String uuid = msg.getUuid();

        InnerConnectedEndpointVO vo = dbf.findByUuid(uuid,InnerConnectedEndpointVO.class);
        dbf.remove(vo);

        APIDeleteInnerEndpointEvent event = new APIDeleteInnerEndpointEvent(msg.getId());
        InnerConnectedEndpointInventory inventory = InnerConnectedEndpointInventory.valueOf(vo);
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
        } else if (msg instanceof APICreateInnerEndpointMsg) {
            validate((APICreateInnerEndpointMsg) msg);
        } else if (msg instanceof APIDeleteInnerEndpointMsg) {
            validate((APIDeleteInnerEndpointMsg) msg);
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
        validateLongitudeAndLatitude(msg.getLongitude());
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
        validateLongitudeAndLatitude(msg.getLongitude());
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

        //判断是否被监控机关联
        SimpleQuery<MonitorHostVO> queryMonitorHost = dbf.createQuery(MonitorHostVO.class);
        queryMonitorHost.add(MonitorHostVO_.nodeUuid,SimpleQuery.Op.EQ,msg.getUuid());
        if (queryMonitorHost.isExists()) {
            throw new ApiMessageInterceptionException(argerr("Monitor host exist,cannot be deleted!"));
        }
    }

    private void validate(APICreateEndpointMsg msg) {
        if (msg.getEndpointType() == EndpointType.CLOUD) {
            if (StringUtils.isEmpty(msg.getCloudType()) || !Q.New(CloudVO.class).eq(CloudVO_.uuid, msg.getCloudType()).isExists()) {
                throw new ApiMessageInterceptionException(argerr(" the cloud endpoint must specify an existing cloud"));
            }
        }
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
        //判断是否有连接点关联
        if(Q.New(InnerConnectedEndpointVO.class).eq(InnerConnectedEndpointVO_.endpointUuid,msg.getUuid()).isExists()){
            throw new ApiMessageInterceptionException(argerr("innerconnected exist,cannot be deleted!"));
        }
        if(Q.New(InnerConnectedEndpointVO.class).eq(InnerConnectedEndpointVO_.connectedEndpointUuid,msg.getUuid()).isExists()){
            throw new ApiMessageInterceptionException(argerr("innerconnected exist,cannot be deleted!"));
        }
    }

    private void validate(APICreateInnerEndpointMsg msg) {
        //判断是否重复添加连接点
        SimpleQuery<InnerConnectedEndpointVO> q = dbf.createQuery(InnerConnectedEndpointVO.class);
        q.add(InnerConnectedEndpointVO_.endpointUuid, SimpleQuery.Op.EQ, msg.getEndpointUuid());
        q.add(InnerConnectedEndpointVO_.connectedEndpointUuid, SimpleQuery.Op.EQ, msg.getConnectedEndpointUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("该连接点已经绑定该互联连接点！"));
        }
        //判断同一个目的连接点的名称是否唯一
        SimpleQuery<InnerConnectedEndpointVO> q2 = dbf.createQuery(InnerConnectedEndpointVO.class);
        q2.add(InnerConnectedEndpointVO_.name, SimpleQuery.Op.EQ, msg.getName());
        q2.add(InnerConnectedEndpointVO_.connectedEndpointUuid, SimpleQuery.Op.EQ, msg.getConnectedEndpointUuid());
        if (q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("该目的连接点的名称已经存在！"));
        }
    }

    private void validate(APIDeleteInnerEndpointMsg msg) {
        //判断该互联连接点是否被跨国通道使用
        InnerConnectedEndpointVO vo = dbf.findByUuid(msg.getUuid(),InnerConnectedEndpointVO.class);
        String innerEndpointUuid = vo.getEndpointUuid();
        String connectedEndpointUuid = vo.getConnectedEndpointUuid();
        List<String> tunnelList = Q.New(TunnelVO.class)
                .eq(TunnelVO_.innerEndpointUuid,innerEndpointUuid)
                .select(TunnelVO_.uuid)
                .listValues();

        if(!tunnelList.isEmpty()){
            for(String tunnelUuid : tunnelList){
                String endpointA = Q.New(TunnelSwitchPortVO.class)
                        .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelUuid)
                        .eq(TunnelSwitchPortVO_.sortTag,"A")
                        .select(TunnelSwitchPortVO_.endpointUuid)
                        .findValue();
                String endpointZ = Q.New(TunnelSwitchPortVO.class)
                        .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelUuid)
                        .eq(TunnelSwitchPortVO_.sortTag,"Z")
                        .select(TunnelSwitchPortVO_.endpointUuid)
                        .findValue();
                if(endpointA.equals(connectedEndpointUuid) || endpointZ.equals(connectedEndpointUuid)){
                    throw new ApiMessageInterceptionException(argerr("该互联连接点至目的连接点已经被通道[%s]使用！",tunnelUuid));
                }

            }
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
