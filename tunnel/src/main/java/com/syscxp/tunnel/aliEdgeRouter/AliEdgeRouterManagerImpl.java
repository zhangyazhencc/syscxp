package com.syscxp.tunnel.aliEdgeRouter;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.EventFacade;
import com.syscxp.core.cloudbus.ResourceDestinationMaker;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.tunnel.AliEdgeRouterConstant;
import com.syscxp.header.tunnel.tunnel.TunnelEO;
import com.syscxp.header.tunnel.tunnel.TunnelState;
import com.syscxp.header.tunnel.aliEdgeRouter.*;
import com.syscxp.utils.CollectionUtils;
import com.syscxp.utils.Utils;
import com.syscxp.utils.function.Function;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
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
        }else if(msg instanceof APIGetAliEdgeRouterMsg){
            handle((APIGetAliEdgeRouterMsg) msg);
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
        } else if(msg instanceof APIListAliTunnelMsg){
            handle((APIListAliTunnelMsg) msg);
        }else if(msg instanceof APIListAliRegionMsg){
            handle((APIListAliRegionMsg) msg);
        } else if(msg instanceof APITerminateAliEdgeRouterMsg){
            handle((APITerminateAliEdgeRouterMsg) msg);
        } else if(msg instanceof APIRecoverAliEdgeRouterMsg){
            handle((APIRecoverAliEdgeRouterMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APIRecoverAliEdgeRouterMsg msg) {
        AliEdgeRouterVO vo = dbf.findByUuid(msg.getUuid(),AliEdgeRouterVO.class);

        String RegionId = vo.getAliRegionId();
        String AliAccessKeyId = AliUserGlobalProperty.ALI_KEY;
        String AliAccessKeySecret = AliUserGlobalProperty.ALI_VALUE;


        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(RegionId,AliAccessKeyId,AliAccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        RecoverVirtualBorderRouterRequest request = new RecoverVirtualBorderRouterRequest();
        request.setVbrId(vo.getVbrUuid());

        RecoverVirtualBorderRouterResponse response;
        try{
            response = client.getAcsResponse(request);

        }catch (Exception e){
            e.printStackTrace();
            throw new ApiMessageInterceptionException(argerr(e.getMessage()));
        }

        APIRecoverAliEdgeRouterEvent evt = new APIRecoverAliEdgeRouterEvent(msg.getId());
        evt.setRouterInventory(AliEdgeRouterInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APITerminateAliEdgeRouterMsg msg) {
        AliEdgeRouterVO vo = dbf.findByUuid(msg.getUuid(),AliEdgeRouterVO.class);

        String RegionId = vo.getAliRegionId();
        String AliAccessKeyId = AliUserGlobalProperty.ALI_KEY;
        String AliAccessKeySecret = AliUserGlobalProperty.ALI_VALUE;

        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(RegionId,AliAccessKeyId,AliAccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        TerminateVirtualBorderRouterRequest request = new TerminateVirtualBorderRouterRequest();
        request.setVbrId(vo.getVbrUuid());

        TerminateVirtualBorderRouterResponse response;

        try{
            response = client.getAcsResponse(request);

        }catch(Exception e){
            e.printStackTrace();
            throw new ApiMessageInterceptionException(argerr(e.getMessage()));
        }

        APITerminateAliEdgeRouterEvent evt = new APITerminateAliEdgeRouterEvent(msg.getId());
        evt.setRouterInventory(AliEdgeRouterInventory.valueOf(vo));
        bus.publish(evt);

    }

    private void handle(APIListAliRegionMsg msg) {
        List<AliRegionInventory> regions = new ArrayList<>();
        String sql = "select distinct aliRegionId,aliRegionName from AliEdgeRouterConfigVO ";

        TypedQuery<Tuple> tfq = dbf.getEntityManager().createQuery(sql, Tuple.class);
        List<Tuple> ts = tfq.getResultList();
        for (Tuple t : ts) {
            AliRegionInventory aliRegionInventoey = new AliRegionInventory();
            aliRegionInventoey.setId(t.get(0, String.class));
            aliRegionInventoey.setName(t.get(1, String.class));
            regions.add(aliRegionInventoey);
        }
        APIListAliRegionReply reply = new APIListAliRegionReply();
        reply.setAliRegionInventories(regions);
        bus.reply(msg,reply);
    }

    @Transactional
    private void handle(APIListAliTunnelMsg msg){
        List<AliTunnelInventory> tunnelQueryList = new ArrayList<AliTunnelInventory>();

        String sql = "select ac from AliEdgeRouterConfigVO ac where ac.aliRegionId = :aliRegionId";
        TypedQuery<AliEdgeRouterConfigVO> configq = dbf.getEntityManager().createQuery(sql, AliEdgeRouterConfigVO.class);
        configq.setParameter("aliRegionId", msg.getAliRegionId());
        List<AliEdgeRouterConfigVO> acs = configq.getResultList();

        List<String> switchPortUuids = CollectionUtils.transformToList(acs, new Function<String, AliEdgeRouterConfigVO>() {
            @Override
            public String call(AliEdgeRouterConfigVO arg) {
                return arg.getSwitchPortUuid();
            }
        });

        sql = "select t.name, t.uuid, ts.vlan, ts.switchPortUuid from TunnelSwitchPortVO ts, TunnelVO t where t.uuid = ts.tunnelUuid " +
                " and t.accountUuid = :accountUuid and t.state = :state" +
                " and ts.switchPortUuid in (:switchPortUuids)";

        TypedQuery<Tuple> tfq = dbf.getEntityManager().createQuery(sql, Tuple.class);
        tfq.setParameter("accountUuid", msg.getAccountUuid());
        tfq.setParameter("state", TunnelState.Enabled);
        tfq.setParameter("switchPortUuids", switchPortUuids);
        List<Tuple> ts = tfq.getResultList();
        for (Tuple t : ts) {
            AliTunnelInventory inventory = new AliTunnelInventory();
            inventory.setTunnelName(t.get(0, String.class));
            inventory.setTunnelUuid(t.get(1, String.class));
            inventory.setVlan(t.get(2, Integer.class));

            String plineUuid = CollectionUtils.find(acs, new Function<String, AliEdgeRouterConfigVO>() {
                @Override
                public String call(AliEdgeRouterConfigVO arg) {
                    if (arg.getSwitchPortUuid().equals(t.get(3, String.class))) {
                        return arg.getPhysicalLineUuid();
                    }
                    return null;
                }
            });
            inventory.setPhysicalLineUuid(plineUuid);

            tunnelQueryList.add(inventory);
        }

        APIListAliTunnelReply reply = new APIListAliTunnelReply();
        reply.setInventory(tunnelQueryList);
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
            vo.setAliRegionName(msg.getAliRegionName());
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
        vo.setAliRegionName(msg.getAliRegionName());
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

    private void handle(APIGetAliEdgeRouterMsg msg){

        AliEdgeRouterInformationInventory inventory = new AliEdgeRouterInformationInventory();
        AliEdgeRouterInventory routerInventory = new AliEdgeRouterInventory();
        AliEdgeRouterVO vo = dbf.findByUuid(msg.getUuid(),AliEdgeRouterVO.class);

        String AliAccessKeyId = null;
        String AliAccessKeySecret = null;
        boolean flag = true;

        if(msg.getAliAccessKeyID() != null && msg.getAliAccessKeySecret() != null){
            AliAccessKeyId = msg.getAliAccessKeyID();
            AliAccessKeySecret = msg.getAliAccessKeySecret();
        }else{
            AliUserVO user = findAliUser(vo);
            if(user != null){
                AliAccessKeyId = user.getAliAccessKeyID();
                AliAccessKeySecret = user.getAliAccessKeySecret();
            }
        }

        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(vo.getAliRegionId(),AliAccessKeyId,AliAccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        // 创建API请求并设置参数
        DescribeVirtualBorderRoutersRequest request = new DescribeVirtualBorderRoutersRequest();

        //组装filter数据
        List<DescribeVirtualBorderRoutersRequest.Filter> list = new ArrayList<DescribeVirtualBorderRoutersRequest.Filter>();
        DescribeVirtualBorderRoutersRequest.Filter filter = new DescribeVirtualBorderRoutersRequest.Filter();
        filter.setKey(AliEdgeRouterConstant.FILTER_KEY);
        List list1 = new ArrayList();
        list1.add(vo.getVbrUuid());
        filter.setValues(list1);
        list.add(filter);

        request.setFilters(list);

        DescribeVirtualBorderRoutersResponse response ;
        try{
            response = client.getAcsResponse(request);

            if(response.getVirtualBorderRouterSet().size() != 0){
                DescribeVirtualBorderRoutersResponse.VirtualBorderRouterType virtualBorderRouterType = response.getVirtualBorderRouterSet().get(0);

                routerInventory.setName(virtualBorderRouterType.getName());
                routerInventory.setVbrUuid(virtualBorderRouterType.getVbrId());
                routerInventory.setDescription(virtualBorderRouterType.getDescription());
                routerInventory.setCreateDate(vo.getCreateDate());
                routerInventory.setVlan(vo.getVlan());
                routerInventory.setAliAccountUuid(vo.getAliAccountUuid());
                routerInventory.setAliRegionId(vo.getAliRegionId());
                routerInventory.setPhysicalLineUuid(vo.getPhysicalLineUuid());
                routerInventory.setTunnelName(vo.getTunnelEO().getName());

                inventory.setAccessPoint(virtualBorderRouterType.getAccessPointId());
                inventory.setStatus(virtualBorderRouterType.getStatus());
                inventory.setPhysicalLineOwerUuid(virtualBorderRouterType.getPhysicalConnectionOwnerUid());
                inventory.setLocalGatewayIp(virtualBorderRouterType.getLocalGatewayIp());
                inventory.setPeerGatewayIp(virtualBorderRouterType.getPeerGatewayIp());
                inventory.setPeeringSubnetMask(virtualBorderRouterType.getPeeringSubnetMask());
            }
        }catch (ClientException e){
            e.printStackTrace();
            if(e.getErrCode().equals("InvalidAccessKeyId.NotFound")||e.getErrCode().equals("IncompleteSignature")){
                DeleteAliUser(AliAccessKeyId,AliAccessKeySecret);
                flag = false;
            }else{
                throw new ApiMessageInterceptionException(argerr(e.getMessage()));
            }
        }

        APIGetAliEdgeRouterReply reply = new APIGetAliEdgeRouterReply();

        if(flag){
            reply.setInventory(inventory);
            reply.setRouterInventory(routerInventory);
        }else {
            reply.setAliIdentityFailure(true);
        }
        bus.reply(msg,reply);
    }

    private void DeleteAliUser(String aliAccessKeyId, String aliAccessKeySecret) {
        UpdateQuery q = UpdateQuery.New(AliUserVO.class);
        q.condAnd(AliUserVO_.aliAccessKeyID, SimpleQuery.Op.EQ, aliAccessKeyId);
        q.condAnd(AliUserVO_.aliAccessKeySecret, SimpleQuery.Op.EQ, aliAccessKeySecret);
        q.delete();
    }

    private AliUserVO findAliUser(AliEdgeRouterVO vo){
        SimpleQuery<AliUserVO> q = dbf.createQuery(AliUserVO.class);
        q.add(AliUserVO_.accountUuid, SimpleQuery.Op.EQ, vo.getAccountUuid());
        q.add(AliUserVO_.aliAccountUuid, SimpleQuery.Op.EQ, vo.getAliAccountUuid());
        AliUserVO user = q.find();

        return user;
    }

    private void handle(APIDeleteAliEdgeRouterMsg msg){
        AliEdgeRouterVO vo = dbf.findByUuid(msg.getUuid(),AliEdgeRouterVO.class);

        boolean flag = true;
        String RegionId = vo.getAliRegionId();
        String AliAccessKeyId = null;
        String AliAccessKeySecret = null;

        if(vo.isCreateFlag()){
            if(msg.getAliAccessKeyID() == null && msg.getAliAccessKeySecret() == null){
                AliUserVO user = findAliUser(vo);
                if(user != null){
                    AliAccessKeyId = user.getAliAccessKeyID();
                    AliAccessKeySecret = user.getAliAccessKeySecret();
                }
            }else{
                AliAccessKeyId = msg.getAliAccessKeyID();
                AliAccessKeySecret = msg.getAliAccessKeySecret();
            }
        }else{
            AliAccessKeyId = AliUserGlobalProperty.ALI_KEY;
            AliAccessKeySecret = AliUserGlobalProperty.ALI_VALUE;
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
            dbf.remove(vo);
        }catch (ClientException e){
            e.printStackTrace();
            if(e.getErrCode().equals("InvalidAccessKeyId.NotFound")||e.getErrCode().equals("IncompleteSignature")){
                DeleteAliUser(AliAccessKeyId,AliAccessKeySecret);
                flag = false;
            } else{
                throw new ApiMessageInterceptionException(argerr(e.getMessage()));
            }
        }

        APIDeleteAliEdgeRouterEvent evt = new APIDeleteAliEdgeRouterEvent(msg.getId());
        if(flag){
            evt.setRouterInventory(AliEdgeRouterInventory.valueOf(vo));
        }else {
            evt.setAliIdentityFailure(true);
        }

        bus.publish(evt);

    }

    private void handle(APIUpdateAliEdgeRouterMsg msg){
        AliEdgeRouterVO vo = dbf.findByUuid(msg.getUuid(),AliEdgeRouterVO.class);
        Boolean flag = true;

        AliEdgeRouterInformationInventory inventory = new AliEdgeRouterInformationInventory();
        APIUpdateAliEdgeRouterEvent evt = new APIUpdateAliEdgeRouterEvent(msg.getId());
        boolean update = false;

        if(msg.getName() !=null){
            vo.setName(msg.getName());
            update = true;
        }

        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if(msg.getLocalGatewayIp() != null && msg.getPeerGatewayIp() != null && msg.getPeeringSubnetMask() != null){
            update = true;
        }

        String AliAccessKeyId = null;
        String AliAccessKeySecret = null;

        if(msg.getAliAccessKeyID()!= null && msg.getAliAccessKeySecret() != null){
            AliAccessKeyId = msg.getAliAccessKeyID();
            AliAccessKeySecret = msg.getAliAccessKeySecret();
        }else{
            SimpleQuery<AliUserVO> q = dbf.createQuery(AliUserVO.class);
            q.add(AliUserVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            q.add(AliUserVO_.aliAccountUuid, SimpleQuery.Op.EQ, vo.getAliAccountUuid());
            AliUserVO user = q.find();
//            AliUserVO user = findAliUser(vo);

            if (user != null) {
                AliAccessKeyId = user.getAliAccessKeyID();
                AliAccessKeySecret = user.getAliAccessKeySecret();
            }
        }

        String RegionId = vo.getAliRegionId();

        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(RegionId, AliAccessKeyId, AliAccessKeySecret);
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
            logger.info(response.toString());

            // 创建API请求并设置参数
            DescribeVirtualBorderRoutersRequest requestGet = new DescribeVirtualBorderRoutersRequest();

            //组装filter数据
            List<DescribeVirtualBorderRoutersRequest.Filter> list = new ArrayList<DescribeVirtualBorderRoutersRequest.Filter>();
            DescribeVirtualBorderRoutersRequest.Filter filter = new DescribeVirtualBorderRoutersRequest.Filter();
            filter.setKey(AliEdgeRouterConstant.FILTER_KEY);
            List list1 = new ArrayList();
            list1.add(vo.getVbrUuid());
            filter.setValues(list1);
            list.add(filter);

            requestGet.setFilters(list);
            DescribeVirtualBorderRoutersResponse responseGet = client.getAcsResponse(requestGet);
            if(responseGet.getVirtualBorderRouterSet().size() != 0){
                DescribeVirtualBorderRoutersResponse.VirtualBorderRouterType virtualBorderRouterType = responseGet.getVirtualBorderRouterSet().get(0);

                inventory.setAccessPoint(virtualBorderRouterType.getAccessPointId());
                inventory.setStatus(virtualBorderRouterType.getStatus());
                inventory.setPhysicalLineOwerUuid(virtualBorderRouterType.getPhysicalConnectionOwnerUid());
                inventory.setLocalGatewayIp(virtualBorderRouterType.getLocalGatewayIp());
                inventory.setPeerGatewayIp(virtualBorderRouterType.getPeerGatewayIp());
                inventory.setPeeringSubnetMask(virtualBorderRouterType.getPeeringSubnetMask());
            }

            if (update){
                vo.setCreateFlag(true);
                vo = dbf.updateAndRefresh(vo);
            }

        }catch (ClientException e){
            e.printStackTrace();

            if(e.getErrCode().equals("InvalidAccessKeyId.NotFound")||e.getErrCode().equals("IncompleteSignature")){
                DeleteAliUser(AliAccessKeyId,AliAccessKeySecret);
                flag = false;
            }else{
                throw new ApiMessageInterceptionException(argerr(e.getMessage()));
            }
        }

        if(flag){
            evt.setRouterInventory(AliEdgeRouterInventory.valueOf(vo));
            evt.setInventory(inventory);
        }else {
            evt.setAliIdentityFailure(true);
        }

        bus.publish(evt);

    }

    @Transactional
    private void handle(APICreateAliEdgeRouterMsg msg){
        AliEdgeRouterVO vo = new AliEdgeRouterVO();

        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setAliAccountUuid(msg.getAliAccountUuid());
        vo.setTunnelEO(dbf.findByUuid(msg.getTunnelUuid(), TunnelEO.class));
        vo.setTunnelUuid(msg.getTunnelUuid());
        vo.setAliRegionId(msg.getAliRegionId());
        vo.setPhysicalLineUuid(msg.getPhysicalLineUuid());
        vo.setVlan(msg.getVlan());

        String RegionId = msg.getAliRegionId();
        String AliAccessKeyId = AliUserGlobalProperty.ALI_KEY;
        String AliAccessKeySecret = AliUserGlobalProperty.ALI_VALUE;

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
//            dbf.persistAndRefresh(vo);
            dbf.getEntityManager().persist(vo);
            dbf.getEntityManager().flush();
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
