package com.syscxp.tunnel.manage;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.*;
import com.aliyuncs.profile.DefaultProfile;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.EventFacade;
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
import com.syscxp.tunnel.header.aliEdgeRouter.*;
import com.syscxp.tunnel.header.endpoint.EndpointVO;
import com.syscxp.tunnel.header.node.NodeVO;
import com.syscxp.tunnel.header.tunnel.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.util.*;

import static com.syscxp.core.Platform.argerr;

public class AliEdgeRouterManagerImpl extends AbstractService implements AliEdgeRouterManager,ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(AliEdgeRouterManagerImpl.class);

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
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }

    }

    private void handleApiMessage(APIMessage msg) {
        if(msg instanceof APICreateAliEdgeRouterMsg){
            handle((APICreateAliEdgeRouterMsg) msg);
        }else if(msg instanceof APIUpdateAliEdgeRouterMsg){
            handle((APIUpdateAliEdgeRouterMsg) msg);
        }else if(msg instanceof APIDeleteAliEdgeRouterMsg){
            handle((APIDeleteAliEdgeRouterMsg) msg);
        }else if(msg instanceof AliEdgeRouterInformationMsg){
            handle((AliEdgeRouterInformationMsg) msg);
        }else if(msg instanceof APISaveAliUserMsg){
            handle((APISaveAliUserMsg) msg);
        }else if(msg instanceof APIUpdateAliUserMsg){
            handle((APIUpdateAliUserMsg) msg);
        }else if(msg instanceof APIDeleteAliUserMsg){
            handle((APIDeleteAliUserMsg) msg);
        }else if(msg instanceof APICreateAliEdgeRouterConfigMsg){
            handle((APICreateAliEdgeRouterConfigMsg) msg);
        }else if(msg instanceof APIUpdateAliEdgeRouterConfigMsg){
            handle((APIUpdateAliEdgeRouterConfigMsg) msg);
        }else if(msg instanceof APIDeleteAliEdgeRouterConfigMsg){
            handle((APIDeleteAliEdgeRouterConfigMsg) msg);
        } else if(msg instanceof TunnelQueryMsg){
            handle((TunnelQueryMsg) msg);
        }
        else {
            bus.dealWithUnknownMessage(msg);
        }

    }


    private void handle(TunnelQueryMsg msg){
        TunnelQueryInventory inventory = new TunnelQueryInventory();
        List<TunnelQueryInventory> TunnelQueryList = new ArrayList<TunnelQueryInventory>();

        SimpleQuery<TunnelVO> q = dbf.createQuery(TunnelVO.class);
        q.add(TunnelVO_.accountUuid, SimpleQuery.Op.EQ,msg.getAccountUuid());
        List<TunnelVO> tunnels = q.list();
        for(TunnelVO tunnel:tunnels){

            if((tunnel.getState().equals("normal"))&&(tunnel.getStatus().equals("normal"))){

                SimpleQuery<TunnelInterfaceVO> query = dbf.createQuery(TunnelInterfaceVO.class);
                query.add(TunnelInterfaceVO_.tunnelUuid, SimpleQuery.Op.EQ,tunnel.getUuid());
                List<TunnelInterfaceVO> TunnelInterfaceVOList = query.list();

//            List<TunnelInterfaceVO> TunnelInterfaceVOList = tunnel.getTunnelInterfaceVO();
                for(TunnelInterfaceVO tunnelInterfaceVO :TunnelInterfaceVOList){
                    InterfaceVO interfaceVO = tunnelInterfaceVO.getInterfaceVO();
                    EndpointVO endpointVO = interfaceVO.getEndpointVO();
                    NodeVO nodeVO = endpointVO.getNodeVO();
                    String region = nodeVO.getCity();

                    if(msg.getAliRegionId().equals(region)){//msg.getAliRegionId 数据的定义

                        SimpleQuery<AliEdgeRouterConfigVO> q1 = dbf.createQuery(AliEdgeRouterConfigVO.class);
                        q1.add(AliEdgeRouterConfigVO_.aliRegionId, SimpleQuery.Op.EQ,msg.getAliRegionId());
                        List<AliEdgeRouterConfigVO> AliEdgeRouterConfigs = q1.list();

                        for(AliEdgeRouterConfigVO aliEdgeRouterConfigVO :AliEdgeRouterConfigs){
                            if(aliEdgeRouterConfigVO.getSwitchPortUuid().toString().equals(interfaceVO.getSwitchPortUuid().toString())){
                                inventory.setTunnelUuid(tunnel.getUuid());
                                inventory.setTunnelName(tunnel.getName());
                                inventory.setAliRegionId(msg.getAliRegionId());
                                inventory.setSwitchPortUuid(interfaceVO.getSwitchPortUuid());
                                inventory.setPhysicalLineUuid(aliEdgeRouterConfigVO.getPhysicalLineUuid());
                                Integer vlan = tunnelInterfaceVO.getVlan();
                                inventory.setVlan(vlan);
                                TunnelQueryList.add(inventory);
                            }
                        }
                    }
                }

            }


        }

        TunnelQueryReply reply = new TunnelQueryReply();
        reply.setInventory(TunnelQueryList);
        bus.reply(msg,reply);

    }

    private void handle(APIDeleteAliEdgeRouterConfigMsg msg){
        AliEdgeRouterConfigVO vo = dbf.findByUuid(msg.getUuid(),AliEdgeRouterConfigVO.class);

        if(vo !=null){
            dbf.remove(vo);
        }

        APICreateAliEdgeRouterConfigEvent evt = new APICreateAliEdgeRouterConfigEvent(msg.getId());
        evt.setInventory(AliEdgeRouterConfigInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateAliEdgeRouterConfigMsg msg){
        AliEdgeRouterConfigVO vo = dbf.findByUuid(msg.getUuid(),AliEdgeRouterConfigVO.class);
        Boolean update = false;
        if(msg.getAliRegionId() != null){
            vo.setAliRegionId(msg.getAliRegionId());
            update = true;
        }

        if(msg.getPhysicalLineUuid()!= null){
            vo.setPhysicalLineUuid(msg.getPhysicalLineUuid());
            update = true;
        }

        if(msg.getSwitchPortUuid() != null){
            vo.setSwitchPortUuid(msg.getSwitchPortUuid());
            update = true;
        }

        if(update)
            vo = dbf.updateAndRefresh(vo);

        APICreateAliEdgeRouterConfigEvent evt = new APICreateAliEdgeRouterConfigEvent(msg.getId());
        evt.setInventory(AliEdgeRouterConfigInventory.valueOf(vo));
        bus.publish(evt);

    }

    private void handle(APICreateAliEdgeRouterConfigMsg msg){
        AliEdgeRouterConfigVO vo = new AliEdgeRouterConfigVO();

        vo.setUuid(Platform.getUuid());
        vo.setAliRegionId(msg.getAliRegionId());
        vo.setPhysicalLineUuid(msg.getPhysicalLineUuid());
        vo.setSwitchPortUuid(msg.getSwitchPortUuid());

        dbf.persistAndRefresh(vo);



        APICreateAliEdgeRouterConfigEvent evt = new APICreateAliEdgeRouterConfigEvent(msg.getId());
        evt.setInventory(AliEdgeRouterConfigInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteAliUserMsg msg){
        AliUserVO vo = dbf.findByUuid(msg.getUuid(),AliUserVO.class);
        if(vo != null){
            dbf.remove(vo);
        }
        APIDeleteAliUserEvent evt = new APIDeleteAliUserEvent(msg.getId());
        evt.setInventory(AliUserInventory.valueOf(vo));
        bus.publish(evt);

    }


    private void handle(APIUpdateAliUserMsg msg){
        AliUserVO vo = dbf.findByUuid(msg.getUuid(),AliUserVO.class);
        boolean update = false;

        if(msg.getAliAccessKeyID() != null){
            vo.setAliAccessKeyID(msg.getAliAccessKeyID());
            update = true;
        }

        if(msg.getAliAccessKeySecret() != null){
            vo.setAliAccessKeySecret(msg.getAliAccessKeySecret());
            update = true;
        }

        if(update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateAliUserEvent evt =  new APIUpdateAliUserEvent(msg.getId());
        evt.setInventory(AliUserInventory.valueOf(vo));
        bus.publish(evt);

    }

    private void handle(APISaveAliUserMsg msg){
        AliUserVO vo = new AliUserVO();

        SimpleQuery<AliUserVO> q = dbf.createQuery(AliUserVO.class);
        q.add(AliUserVO_.accountUuid, SimpleQuery.Op.EQ,msg.getAccountUuid());
        q.add(AliUserVO_.aliAccountUuid, SimpleQuery.Op.EQ,msg.getAliAccountUuid());
        AliUserVO user = q.find();

        if(user != null){
            dbf.remove(user);
        }

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setAliAccountUuid(msg.getAliAccountUuid());
        vo.setAliAccessKeyID(msg.getAliAccessKeyID());
        vo.setAliAccessKeySecret(msg.getAliAccessKeySecret());

        dbf.persistAndRefresh(vo);

        APISaveAliUserEvent evt = new APISaveAliUserEvent(msg.getId());
        evt.setInventory(AliUserInventory.valueOf(vo));
        bus.publish(evt);

    }

    private void handle(AliEdgeRouterInformationMsg msg){

        AliEdgeRouterInformationInventory inventory = new AliEdgeRouterInformationInventory();
        String AliAccessKeyId ;
        String AliAccessKeySecret ;


        if(msg.getAliAccessKeyID() != null && msg.getAliAccessKeySecret() != null){
            AliAccessKeyId = msg.getAliAccessKeyID();
            AliAccessKeySecret = msg.getAliAccessKeySecret();
        }else{
            SimpleQuery<AliUserVO> q = dbf.createQuery(AliUserVO.class);
            q.add(AliUserVO_.aliAccountUuid, SimpleQuery.Op.EQ,msg.getAliAccountUuid());
            q.add(AliUserVO_.accountUuid, SimpleQuery.Op.EQ,msg.getAccountUuid());
            AliUserVO user = q.find();
            AliAccessKeyId = user.getAliAccessKeyID();
            AliAccessKeySecret = user.getAliAccessKeySecret();
        }

        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(msg.getAliRegionId(),AliAccessKeyId,AliAccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        // 创建API请求并设置参数
        DescribeVirtualBorderRoutersRequest request = new DescribeVirtualBorderRoutersRequest();

        List<DescribeVirtualBorderRoutersRequest.Filter> list = new ArrayList<DescribeVirtualBorderRoutersRequest.Filter>();
        DescribeVirtualBorderRoutersRequest.Filter filter = new DescribeVirtualBorderRoutersRequest.Filter();
        filter.setKey("VbrId");
        List list1 = new ArrayList();
        list1.add(msg.getVbrUuid());
        filter.setValues(list1);
        list.add(filter);

        request.setFilters(list);
        //组装filter数据
//        List list = new ArrayList();
//        List list1 = new ArrayList();
//        Map map = new Hashtable();
//        map.put("key","VbrId");
//        list1.add(msg.getVbrUuid());
//        map.put("values",list1);
//        list.add(map);
//
//        System.out.println(list);

        DescribeVirtualBorderRoutersResponse response ;
        try{
            response = client.getAcsResponse(request);
            inventory.setName(response.getVirtualBorderRouterSet().get(0).getName());
            inventory.setVbrUuid(response.getVirtualBorderRouterSet().get(0).getVbrId());
            inventory.setAccessPoint(response.getVirtualBorderRouterSet().get(0).getAccessPointId());
            inventory.setStatus(response.getVirtualBorderRouterSet().get(0).getStatus());
            inventory.setDescription(response.getVirtualBorderRouterSet().get(0).getDescription());
            inventory.setCreateDate(response.getVirtualBorderRouterSet().get(0).getCreationTime());
            inventory.setPhysicalLineOwerUuid(response.getVirtualBorderRouterSet().get(0).getPhysicalConnectionOwnerUid());
            inventory.setLocalGatewayIp(response.getVirtualBorderRouterSet().get(0).getLocalGatewayIp());
            inventory.setPeerGatewayIp(response.getVirtualBorderRouterSet().get(0).getPeerGatewayIp());
            inventory.setPeeringSubnetMask(response.getVirtualBorderRouterSet().get(0).getPeeringSubnetMask());
            inventory.setVlan(response.getVirtualBorderRouterSet().get(0).getVlanId());

        }catch (Exception e){
            e.printStackTrace();
            throw new ApiMessageInterceptionException(argerr(e.getMessage()));
        }

        AliEdgeRouterInformationReply reply = new AliEdgeRouterInformationReply();
        reply.setInventory(inventory);
        bus.reply(msg,reply);


    }


    private void handle(APIDeleteAliEdgeRouterMsg msg){
        AliEdgeRouterEO eo = dbf.findByUuid(msg.getUuid(),AliEdgeRouterEO.class);
        AliEdgeRouterVO vo = dbf.findByUuid(msg.getUuid(),AliEdgeRouterVO.class);

        String RegionId = vo.getAliRegionId();
        String AliAccessKeyId ;
        String AliAccessKeySecret ;

        if(msg.getFlag() == true && msg.getAliAccessKeyID() == null && msg.getAliAccessKeySecret() == null){
            SimpleQuery<AliUserVO> q = dbf.createQuery(AliUserVO.class);
            q.add(AliUserVO_.accountUuid, SimpleQuery.Op.EQ,msg.getAccountUuid());
            q.add(AliUserVO_.aliAccountUuid, SimpleQuery.Op.EQ,vo.getAliAccountUuid());
            AliUserVO user = q.find();

            AliAccessKeyId = user.getAliAccessKeyID();
            AliAccessKeySecret = user.getAliAccessKeySecret();

        }else if(msg.getFlag() == true && msg.getAliAccessKeyID() != null && msg.getAliAccessKeySecret() != null){
            AliAccessKeyId = msg.getAliAccessKeyID();
            AliAccessKeySecret = msg.getAliAccessKeySecret();
        }
        else{
            SimpleQuery<AliUserVO> q = dbf.createQuery(AliUserVO.class);
            q.add(AliUserVO_.accountUuid, SimpleQuery.Op.EQ,"admin");
            q.add(AliUserVO_.aliAccountUuid, SimpleQuery.Op.EQ,"admin");
            AliUserVO user = q.find();

            AliAccessKeyId = user.getAliAccessKeyID();
            AliAccessKeySecret = user.getAliAccessKeySecret();

        }

        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(RegionId,AliAccessKeyId,AliAccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        // 创建API请求并设置参数
        DeleteVirtualBorderRouterRequest request = new DeleteVirtualBorderRouterRequest();
        request.setVbrId(vo.getVbrUuid());

        DeleteVirtualBorderRouterResponse response;

        try{
            response = client.getAcsResponse(request);
            eo.setDeleted(1);
            dbf.updateAndRefresh(eo);

        }catch (Exception e){
            e.printStackTrace();
            throw new ApiMessageInterceptionException(argerr(e.getMessage()));
        }

        APIDeleteAliEdgeRouterEvent evt = new APIDeleteAliEdgeRouterEvent(msg.getId());
        evt.setInventory(AliEdgeRouterInventory.valueOf(vo));
        bus.publish(evt);

    }

    private void handle(APIUpdateAliEdgeRouterMsg msg){
        AliEdgeRouterVO vo = dbf.findByUuid(msg.getUuid(),AliEdgeRouterVO.class);
        boolean update = false;

        if(msg.getName() !=null){
            vo.setName(msg.getName());
            update = true;
        }

        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
            update = true;
        }
        String AliAccessKeyId;
        String AliAccessKeySecret;

        if(msg.getAliAccessKeyID()!= null && msg.getAliAccessKeySecret() != null){
            AliAccessKeyId = msg.getAliAccessKeyID();
            AliAccessKeySecret = msg.getAliAccessKeySecret();
        }else{
            SimpleQuery<AliUserVO> q = dbf.createQuery(AliUserVO.class);
            q.add(AliUserVO_.accountUuid, SimpleQuery.Op.EQ,msg.getAccountUuid());
            q.add(AliUserVO_.aliAccountUuid, SimpleQuery.Op.EQ,vo.getAliAccountUuid());
            AliUserVO user = q.find();

            AliAccessKeyId = user.getAliAccessKeyID();
            AliAccessKeySecret = user.getAliAccessKeySecret();
        }


        String RegionId = vo.getAliRegionId();


        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(RegionId,AliAccessKeyId,AliAccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        // 创建API请求并设置参数
        ModifyVirtualBorderRouterAttributeRequest request = new ModifyVirtualBorderRouterAttributeRequest();
        request.setVbrId(vo.getVbrUuid());
        request.setLocalGatewayIp(msg.getLocalGatewayIp());
        request.setPeerGatewayIp(msg.getPeerGatewayIp());
        request.setPeeringSubnetMask(msg.getPeeringSubnetMask());
        request.setName(vo.getName());
        request.setDescription(vo.getDescription());

        ModifyVirtualBorderRouterAttributeResponse response;
        try{
            response = client.getAcsResponse(request);
            if (update)
                vo = dbf.updateAndRefresh(vo);

        }catch (Exception e){
            e.printStackTrace();
            throw new ApiMessageInterceptionException(argerr(e.getMessage()));
        }

        APICreateAliEdgeRouterEvent evt = new APICreateAliEdgeRouterEvent(msg.getId());
        evt.setInventory(AliEdgeRouterInventory.valueOf(vo));
        bus.publish(evt);

    }

    private void handle(APICreateAliEdgeRouterMsg msg){
        AliEdgeRouterVO vo = new AliEdgeRouterVO();

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setAliAccountUuid(msg.getAliAccountUuid());
        vo.setTunnelUuid(msg.getTunnelUuid());
        vo.setAliRegionId(msg.getAliRegionId());
        vo.setPhysicalLineUuid(msg.getPhysicalLineUuid());
        vo.setVlan(msg.getVlan());

        SimpleQuery<AliUserVO> q = dbf.createQuery(AliUserVO.class);
        q.add(AliUserVO_.accountUuid, SimpleQuery.Op.EQ,"admin");
        q.add(AliUserVO_.aliAccountUuid, SimpleQuery.Op.EQ,"admin");
        AliUserVO user = q.find();


        String RegionId = msg.getAliRegionId();
        String AliAccessKeyId = user.getAliAccessKeyID();
        String AliAccessKeySecret = user.getAliAccessKeySecret();


        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(RegionId,AliAccessKeyId,AliAccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        // 创建API请求并设置参数
        CreateVirtualBorderRouterRequest VBR = new CreateVirtualBorderRouterRequest();
        VBR.setPhysicalConnectionId(msg.getPhysicalLineUuid());
        VBR.setVbrOwnerId(Long.parseLong(msg.getAliAccountUuid()));
        VBR.setVlanId(msg.getVlan());
        VBR.setClientToken(msg.getAliAccountUuid());
        CreateVirtualBorderRouterResponse response;
        try{
            response = client.getAcsResponse(VBR);
            vo.setVbrUuid(response.getVbrId());
            dbf.persistAndRefresh(vo);
        }catch(Exception e){
            e.printStackTrace();
            throw new ApiMessageInterceptionException(argerr(e.getMessage()));
        }

        APICreateAliEdgeRouterEvent evt = new APICreateAliEdgeRouterEvent(msg.getId());
        evt.setInventory(AliEdgeRouterInventory.valueOf(vo));
        bus.publish(evt);

    }


    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(AliEdgeRouterConstant.SERVICE_ID);
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
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return msg;
    }
}
