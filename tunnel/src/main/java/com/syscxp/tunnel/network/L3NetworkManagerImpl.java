package com.syscxp.tunnel.network;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.job.JobQueueEntryVO;
import com.syscxp.core.job.JobQueueEntryVO_;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.configuration.MotifyType;
import com.syscxp.header.configuration.ResourceMotifyRecordVO;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.header.tunnel.tunnel.InterfaceVO;
import com.syscxp.header.tunnel.tunnel.TaskResourceVO;
import com.syscxp.header.tunnel.tunnel.TaskType;
import com.syscxp.tunnel.network.job.CreateL3EndpointRollBackJob;
import com.syscxp.tunnel.tunnel.TunnelBase;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.syscxp.core.Platform.argerr;


public class L3NetworkManagerImpl extends AbstractService implements L3NetworkManager, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(L3NetworkManagerImpl.class);


    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private JobQueueFacade jobf;

    private VOAddAllOfMsg vOAddAllOfMsg = new VOAddAllOfMsg();

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
        L3NetworkControllerBase base = new L3NetworkControllerBase();
        base.handleMessage(msg);
    }

    public void handleApiMessage(APIMessage msg) {
        if(msg instanceof APICreateL3NetworkMsg){
            handle((APICreateL3NetworkMsg) msg);
        }else if(msg instanceof APIUpdateL3NetworkMsg){
            handle((APIUpdateL3NetworkMsg) msg);
        }else if(msg instanceof APIDeleteL3NetworkMsg){
            handle((APIDeleteL3NetworkMsg) msg);
        }else if(msg instanceof APICreateL3EndPointMsg){
            handle((APICreateL3EndPointMsg) msg);
        }else if(msg instanceof APIUpdateL3EndpointIPMsg){
            handle((APIUpdateL3EndpointIPMsg) msg);
        }else if(msg instanceof APIUpdateL3EndpointBandwidthMsg){
            handle((APIUpdateL3EndpointBandwidthMsg) msg);
        }else if(msg instanceof APIDeleteL3EndPointMsg){
            handle((APIDeleteL3EndPointMsg) msg);
        }else if(msg instanceof APICreateL3RouteMsg){
            handle((APICreateL3RouteMsg) msg);
        }else if(msg instanceof APIDeleteL3RouteMsg){
            handle((APIDeleteL3RouteMsg) msg);
        }
        else {
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APICreateL3NetworkMsg msg) {
        L3NetworkBase l3NetworkBase = new L3NetworkBase();
        APICreateL3NetworkEvent evt = new APICreateL3NetworkEvent(msg.getId());

        L3NetworkVO vo = new L3NetworkVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setCode(msg.getAccountUuid().substring(0,10));
        vo.setVid(l3NetworkBase.getVidAuto());
        vo.setType("MPLSVPN");
        vo.setEndPointNum(0);
        vo.setDescription(msg.getDescription());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setMaxModifies(CoreGlobalProperty.L3NETWORK_MAX_MOTIFIES);
        vo.setExpireDate(null);
        vo = dbf.persistAndRefresh(vo);

        evt.setInventory(L3NetworkInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateL3NetworkMsg msg) {
        APIUpdateL3NetworkEvent event = new APIUpdateL3NetworkEvent(msg.getId());
        L3NetworkVO vo = dbf.findByUuid(msg.getUuid(),L3NetworkVO.class);
        vOAddAllOfMsg.addAll(msg,vo);
        event.setInventory(L3NetworkInventory.valueOf(dbf.updateAndRefresh(vo)));
        bus.publish(event);
    }

    private void handle(APIDeleteL3NetworkMsg msg) {
        APIDeleteL3NetworkEvent evt = new APIDeleteL3NetworkEvent(msg.getId());
        L3NetworkVO vo = dbf.findByUuid(msg.getUuid(),L3NetworkVO.class);

        dbf.remove(vo);

        bus.publish(evt);
    }

    private void handle(APICreateL3EndPointMsg msg) {
        APICreateL3EndPointEvent evt = new APICreateL3EndPointEvent(msg.getId());

        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        String l3EndpointUuid = doCreateL3EndPointVO(msg);
        L3EndPointVO vo = dbf.findByUuid(l3EndpointUuid, L3EndPointVO.class);

        l3NetworkBase.updateEndPointNum(msg.getL3NetworkUuid());

        evt.setInventory(L3EndPointInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    private String doCreateL3EndPointVO(APICreateL3EndPointMsg msg){
        TunnelBase tunnelBase = new TunnelBase();
        L3NetworkBase l3NetworkBase = new L3NetworkBase();


        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        L3NetworkVO l3NetworkVO = dbf.findByUuid(msg.getL3NetworkUuid(), L3NetworkVO.class);
        InterfaceVO interfaceVO = dbf.findByUuid(msg.getInterfaceUuid(), InterfaceVO.class);

        String switchPortUuid = interfaceVO.getSwitchPortUuid();
        String physicalSwitchUuid = tunnelBase.getPhysicalSwitchBySwitchPortUuid(interfaceVO.getSwitchPortUuid()).getUuid();

        Integer vlan = l3NetworkBase.getVlanForL3(switchPortUuid, physicalSwitchUuid);
        if (vlan == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN，请联系系统管理员",switchPortUuid));
        }

        L3EndPointVO vo = new L3EndPointVO();
        vo.setUuid(Platform.getUuid());
        vo.setL3NetworkUuid(msg.getL3NetworkUuid());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setBandwidthOffering(msg.getBandwidthOfferingUuid());
        vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
        vo.setRouteType("STATIC");
        vo.setState(L3EndpointState.Disabled);
        vo.setStatus(L3EndpointStatus.Disconnected);
        vo.setMaxRouteNum(CoreGlobalProperty.L3_MAX_ROUTENUM);
        vo.setLocalIP(null);
        vo.setRemoteIp(null);
        vo.setNetmask(null);
        vo.setInterfaceUuid(msg.getInterfaceUuid());
        vo.setSwitchPortUuid(switchPortUuid);
        vo.setPhysicalSwitchUuid(physicalSwitchUuid);
        vo.setVlan(vlan);
        vo.setRd("58991:"+Integer.toString(l3NetworkVO.getVid()));

        L3RtVO rtVO = new L3RtVO();
        rtVO.setUuid(Platform.getUuid());
        rtVO.setL3EndPointUuid(vo.getUuid());
        rtVO.setImpor("58991:"+Integer.toString(l3NetworkVO.getVid()));
        rtVO.setExport("58991:"+Integer.toString(l3NetworkVO.getVid()));

        dbf.getEntityManager().persist(vo);
        dbf.getEntityManager().persist(rtVO);

        return vo.getUuid();
    }

    private void handle(APIUpdateL3EndpointIPMsg msg){
        APIUpdateL3EndpointIPEvent evt = new APIUpdateL3EndpointIPEvent(msg.getId());

        String l3EndpointUuid = doUpdateL3EndpointIP(msg);
        L3EndPointVO vo = dbf.findByUuid(l3EndpointUuid, L3EndPointVO.class);

        afterUpdateL3EndpointIP(vo, msg, new ReturnValueCompletion<L3EndPointInventory>(null) {

            @Override
            public void success(L3EndPointInventory inv) {
                evt.setInventory(inv);
                bus.publish(evt);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                evt.setError(errorCode);
                bus.publish(evt);
            }
        });

    }

    @Transactional
    private String doUpdateL3EndpointIP(APIUpdateL3EndpointIPMsg msg){
        L3EndPointVO vo = dbf.findByUuid(msg.getUuid(), L3EndPointVO.class);

        vo.setRemoteIp(msg.getRemoteIp());
        vo.setLocalIP(msg.getLocalIP());
        vo.setNetmask(msg.getNetmask());

        dbf.getEntityManager().merge(vo);

        if(Q.New(L3RouteVO.class).eq(L3RouteVO_.l3EndPointUuid, msg.getUuid()).isExists()){
            List<L3RouteVO> l3RouteVOs = Q.New(L3RouteVO.class).eq(L3RouteVO_.l3EndPointUuid, msg.getUuid()).list();
            for(L3RouteVO l3RouteVO : l3RouteVOs){
                l3RouteVO.setNextIp(msg.getRemoteIp());
                dbf.getEntityManager().merge(l3RouteVO);
            }
        }

        return msg.getUuid();
    }

    private void afterUpdateL3EndpointIP(L3EndPointVO vo, APIUpdateL3EndpointIPMsg msg, ReturnValueCompletion<L3EndPointInventory> completion){
        L3NetworkBase l3NetworkBase = new L3NetworkBase();
        L3NetworkTaskBase taskBase = new L3NetworkTaskBase();

        if(vo.getState() == L3EndpointState.Disabled){
            //修改状况
            vo.setState(L3EndpointState.Deploying);
            vo.setStatus(L3EndpointStatus.Disconnected);
            dbf.updateAndRefresh(vo);

            if(l3NetworkBase.isFirstSetEndpointIP(vo)){

                taskBase.taskCreateL3Endpoint(vo,new ReturnValueCompletion<L3EndPointInventory>(null) {
                    @Override
                    public void success(L3EndPointInventory inv) {
                        completion.success(inv);
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        completion.fail(errorCode);
                    }
                });

            }else{
                if(l3NetworkBase.isChangeEndpointIP(vo, msg.getLocalIP(), msg.getRemoteIp(), msg.getNetmask())){

                    taskBase.taskCreateL3Endpoint(vo,new ReturnValueCompletion<L3EndPointInventory>(null) {
                        @Override
                        public void success(L3EndPointInventory inv) {
                            completion.success(inv);
                        }

                        @Override
                        public void fail(ErrorCode errorCode) {
                            completion.fail(errorCode);
                        }
                    });

                }else{
                    jobf.removeJob(vo.getUuid(), CreateL3EndpointRollBackJob.class);

                    taskBase.taskCreateL3Endpoint(vo,new ReturnValueCompletion<L3EndPointInventory>(null) {
                        @Override
                        public void success(L3EndPointInventory inv) {
                            completion.success(inv);
                        }

                        @Override
                        public void fail(ErrorCode errorCode) {
                            completion.fail(errorCode);
                        }
                    });
                }
            }

        }else{
            //修改状况
            vo.setState(L3EndpointState.Deploying);
            vo.setStatus(L3EndpointStatus.Disconnected);
            dbf.updateAndRefresh(vo);

            taskBase.taskUpdateL3EndpointIP(vo.getUuid());
            completion.success(L3EndPointInventory.valueOf(dbf.reload(vo)));

        }

    }

    private void handle(APIUpdateL3EndpointBandwidthMsg msg){
        APIUpdateL3EndpointBandwidthEvent evt = new APIUpdateL3EndpointBandwidthEvent(msg.getId());
        L3NetworkTaskBase taskBase = new L3NetworkTaskBase();

        L3EndPointVO vo = dbf.findByUuid(msg.getUuid(), L3EndPointVO.class);

        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);

        //调整次数记录表
        ResourceMotifyRecordVO record = new ResourceMotifyRecordVO();
        record.setUuid(Platform.getUuid());
        record.setResourceUuid(vo.getL3NetworkUuid());
        record.setResourceType("L3NetworkVO");
        record.setOpAccountUuid(msg.getSession().getAccountUuid());
        record.setMotifyType(bandwidthOfferingVO.getBandwidth() > vo.getBandwidth() ? MotifyType.UPGRADE : MotifyType.DOWNGRADE);
        dbf.persistAndRefresh(record);

        vo.setBandwidthOffering(msg.getBandwidthOfferingUuid());
        vo.setBandwidth(bandwidthOfferingVO.getBandwidth());
        vo.setState(L3EndpointState.Deploying);
        vo.setStatus(L3EndpointStatus.Disconnected);
        vo = dbf.updateAndRefresh(vo);

        taskBase.taskUpdateL3EndpointBandwidth(vo.getUuid());

        evt.setInventory(L3EndPointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteL3EndPointMsg msg) {
        APIDeleteL3EndPointEvent evt = new APIDeleteL3EndPointEvent(msg.getId());
        L3NetworkTaskBase taskBase = new L3NetworkTaskBase();

        L3EndPointVO vo = dbf.findByUuid(msg.getUuid(), L3EndPointVO.class);


        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        if(vo.getState() == L3EndpointState.Enabled){
            vo.setState(L3EndpointState.Deploying);
            vo.setStatus(L3EndpointStatus.Disconnected);
            vo = dbf.updateAndRefresh(vo);

            taskBase.taskDeleteL3Endpoint(vo.getUuid());

            evt.setInventory(L3EndPointInventory.valueOf(vo));
            bus.publish(evt);
        }else{

            //删除连接点
            l3NetworkBase.deleteL3EndpointDB(msg.getUuid());
            l3NetworkBase.updateEndPointNum(vo.getL3NetworkUuid());

            bus.publish(evt);
        }

    }

    private void handle(APICreateL3RouteMsg msg) {
        APICreateL3RouteEvent evt = new APICreateL3RouteEvent(msg.getId());
        L3NetworkTaskBase taskBase = new L3NetworkTaskBase();
        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        L3EndPointVO l3EndPointVO = dbf.findByUuid(msg.getL3EndPointUuid(), L3EndPointVO.class);

        Integer index = l3NetworkBase.getIndexForRoute(msg.getL3EndPointUuid());

        L3RouteVO vo = new L3RouteVO();
        vo.setUuid(Platform.getUuid());
        vo.setL3EndPointUuid(msg.getL3EndPointUuid());
        vo.setCidr(msg.getCidr());
        vo.setNextIp(l3EndPointVO.getRemoteIp());
        vo.setIndexNum(index);

        vo = dbf.persistAndRefresh(vo);

        l3EndPointVO.setState(L3EndpointState.Deploying);
        l3EndPointVO.setStatus(L3EndpointStatus.Disconnected);
        l3EndPointVO = dbf.updateAndRefresh(l3EndPointVO);

        taskBase.taskAddL3EndpointRoutes(vo.getUuid());

        evt.setInventory(L3EndPointInventory.valueOf(l3EndPointVO));
        bus.publish(evt);
    }

    private void handle(APIDeleteL3RouteMsg msg) {
        APIDeleteL3RouteEvent evt = new APIDeleteL3RouteEvent(msg.getId());
        L3NetworkTaskBase taskBase = new L3NetworkTaskBase();

        L3RouteVO vo = dbf.findByUuid(msg.getUuid(), L3RouteVO.class);
        L3EndPointVO l3EndPointVO = dbf.findByUuid(vo.getL3EndPointUuid(), L3EndPointVO.class);

        l3EndPointVO.setState(L3EndpointState.Deploying);
        l3EndPointVO.setStatus(L3EndpointStatus.Disconnected);
        l3EndPointVO = dbf.updateAndRefresh(l3EndPointVO);

        taskBase.taskDeleteL3EndpointRoutes(vo.getUuid());

        evt.setInventory(L3EndPointInventory.valueOf(l3EndPointVO));
        bus.publish(evt);
    }



    @Override
    public String getId() {
        return bus.makeLocalServiceId(L3NetWorkConstant.SERVICE_ID);
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
        L3NetworkValidateBase base = new L3NetworkValidateBase();
        if (msg instanceof APICreateL3NetworkMsg) {
            base.validate((APICreateL3NetworkMsg) msg);
        }else if(msg instanceof APIUpdateL3NetworkMsg){
            base.validate((APIUpdateL3NetworkMsg) msg);
        }else if(msg instanceof APIDeleteL3NetworkMsg){
            base.validate((APIDeleteL3NetworkMsg) msg);
        }else if(msg instanceof APICreateL3EndPointMsg){
            base.validate((APICreateL3EndPointMsg) msg);
        }else if(msg instanceof APIUpdateL3EndpointIPMsg){
            base.validate((APIUpdateL3EndpointIPMsg) msg);
        }else if(msg instanceof APIUpdateL3EndpointBandwidthMsg){
            base.validate((APIUpdateL3EndpointBandwidthMsg) msg);
        }else if(msg instanceof APIDeleteL3EndPointMsg){
            base.validate((APIDeleteL3EndPointMsg) msg);
        }else if(msg instanceof APICreateL3RouteMsg){
            base.validate((APICreateL3RouteMsg) msg);
        }else if(msg instanceof APIDeleteL3RouteMsg){
            base.validate((APIDeleteL3RouteMsg) msg);
        }

        return msg;
    }
}