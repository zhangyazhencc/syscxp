package com.syscxp.tunnel.network;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.errorcode.ErrorFacade;
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
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.header.tunnel.tunnel.InterfaceVO;
import com.syscxp.tunnel.tunnel.TunnelBase;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;
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
        }else if(msg instanceof APICreateL3NetworkManualMsg){
            handle((APICreateL3NetworkManualMsg) msg);
        }else if(msg instanceof APIGetL3VidAutoMsg){
            handle((APIGetL3VidAutoMsg) msg);
        }else if(msg instanceof APIGetL3VlanAutoMsg){
            handle((APIGetL3VlanAutoMsg) msg);
        }else if(msg instanceof APIUpdateL3NetworkMsg){
            handle((APIUpdateL3NetworkMsg) msg);
        }else if(msg instanceof APIDeleteL3NetworkMsg){
            handle((APIDeleteL3NetworkMsg) msg);
        }else if(msg instanceof APICreateL3EndPointMsg){
            handle((APICreateL3EndPointMsg) msg);
        }else if(msg instanceof APICreateL3EndPointManualMsg){
            handle((APICreateL3EndPointManualMsg) msg);
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
        }else if(msg instanceof APIEnableL3EndPointMsg){
            handle((APIEnableL3EndPointMsg) msg);
        }else if(msg instanceof APIDisableL3EndPointMsg){
            handle((APIDisableL3EndPointMsg) msg);
        } else {
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

    private void handle(APICreateL3NetworkManualMsg msg){
        APICreateL3NetworkManualEvent evt = new APICreateL3NetworkManualEvent(msg.getId());

        L3NetworkVO vo = new L3NetworkVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setOwnerAccountUuid(msg.getAccountUuid());
        vo.setName(msg.getName());
        vo.setCode(msg.getAccountUuid().substring(0,10));
        vo.setVid(msg.getVid());
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

    private void handle(APIGetL3VidAutoMsg msg){
        APIGetL3VidAutoReply reply = new APIGetL3VidAutoReply();

        L3NetworkBase l3NetworkBase = new L3NetworkBase();
        reply.setVid(l3NetworkBase.getVidAuto());

        bus.reply(msg, reply);
    }

    private void handle(APIGetL3VlanAutoMsg msg){
        APIGetL3VlanAutoReply reply = new APIGetL3VlanAutoReply();

        TunnelBase tunnelBase = new TunnelBase();
        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        InterfaceVO interfaceVO = dbf.findByUuid(msg.getInterfaceUuid(), InterfaceVO.class);

        String switchPortUuid = interfaceVO.getSwitchPortUuid();
        String physicalSwitchUuid = tunnelBase.getPhysicalSwitchBySwitchPortUuid(interfaceVO.getSwitchPortUuid()).getUuid();

        Integer vlan = l3NetworkBase.getVlanForL3(switchPortUuid, physicalSwitchUuid);
        if (vlan == 0) {
            throw new ApiMessageInterceptionException(argerr("该端口[%s]所属虚拟交换机下已无可使用的VLAN，请联系系统管理员",switchPortUuid));
        }

        reply.setVlan(vlan);
        bus.reply(msg, reply);

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

    private void handle(APICreateL3EndPointManualMsg msg){
        APICreateL3EndPointManualEvent evt = new APICreateL3EndPointManualEvent(msg.getId());

        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        String l3EndpointUuid = doCreateL3EndPointManualVO(msg);
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
        vo.setIpCidr(null);
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

    @Transactional
    private String doCreateL3EndPointManualVO(APICreateL3EndPointManualMsg msg){
        TunnelBase tunnelBase = new TunnelBase();

        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        L3NetworkVO l3NetworkVO = dbf.findByUuid(msg.getL3NetworkUuid(), L3NetworkVO.class);
        InterfaceVO interfaceVO = dbf.findByUuid(msg.getInterfaceUuid(), InterfaceVO.class);

        String switchPortUuid = interfaceVO.getSwitchPortUuid();
        String physicalSwitchUuid = tunnelBase.getPhysicalSwitchBySwitchPortUuid(interfaceVO.getSwitchPortUuid()).getUuid();

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
        vo.setIpCidr(null);
        vo.setInterfaceUuid(msg.getInterfaceUuid());
        vo.setSwitchPortUuid(switchPortUuid);
        vo.setPhysicalSwitchUuid(physicalSwitchUuid);
        vo.setVlan(msg.getVlan());
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

        evt.setInventory(L3EndPointInventory.valueOf(vo));
        bus.publish(evt);

    }

    @Transactional
    private String doUpdateL3EndpointIP(APIUpdateL3EndpointIPMsg msg){
        L3EndPointVO vo = dbf.findByUuid(msg.getUuid(), L3EndPointVO.class);

        vo.setRemoteIp(msg.getRemoteIp());
        vo.setLocalIP(msg.getLocalIP());
        vo.setNetmask(msg.getNetmask());
        vo.setIpCidr(NetworkUtils.getIpCidrFromIpv4Netmask(msg.getLocalIP(), msg.getNetmask()));

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

    private void handle(APIUpdateL3EndpointBandwidthMsg msg){
        APIUpdateL3EndpointBandwidthEvent evt = new APIUpdateL3EndpointBandwidthEvent(msg.getId());

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
        vo = dbf.updateAndRefresh(vo);

        if(vo.getState() == L3EndpointState.Enabled){
            vo.setState(L3EndpointState.Deploying);
            vo = dbf.updateAndRefresh(vo);

            new L3NetworkTaskBase().taskUpdateL3EndpointBandwidth(vo.getUuid());
        }

        evt.setInventory(L3EndPointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteL3EndPointMsg msg) {
        APIDeleteL3EndPointEvent evt = new APIDeleteL3EndPointEvent(msg.getId());

        L3EndPointVO vo = dbf.findByUuid(msg.getUuid(), L3EndPointVO.class);

        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        l3NetworkBase.deleteL3EndpointDB(msg.getUuid());
        l3NetworkBase.updateEndPointNum(vo.getL3NetworkUuid());

        bus.publish(evt);

    }

    private void handle(APICreateL3RouteMsg msg) {
        APICreateL3RouteEvent evt = new APICreateL3RouteEvent(msg.getId());
        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        L3EndPointVO l3EndPointVO = dbf.findByUuid(msg.getL3EndPointUuid(), L3EndPointVO.class);

        Integer index = l3NetworkBase.getIndexForRoute(msg.getL3EndPointUuid());

        String[] cidrArr = msg.getCidr().split("/");
        String truthCidr = NetworkUtils.getIpCidrFromIpv4Netmask(cidrArr[0],NetworkUtils.netmaskFromInt(cidrArr[1]));

        L3RouteVO vo = new L3RouteVO();
        vo.setUuid(Platform.getUuid());
        vo.setL3EndPointUuid(msg.getL3EndPointUuid());
        vo.setCidr(msg.getCidr());
        vo.setTruthCidr(truthCidr);
        vo.setNextIp(l3EndPointVO.getRemoteIp());
        vo.setIndexNum(index);

        vo = dbf.persistAndRefresh(vo);

        if(l3EndPointVO.getState() == L3EndpointState.Enabled){

            new L3NetworkTaskBase().taskAddL3EndpointRoutes(vo, new ReturnValueCompletion<L3EndPointInventory>(null) {
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

        }else{
            evt.setInventory(L3EndPointInventory.valueOf(dbf.reload(l3EndPointVO)));
            bus.publish(evt);
        }


    }

    private void handle(APIDeleteL3RouteMsg msg) {
        APIDeleteL3RouteEvent evt = new APIDeleteL3RouteEvent(msg.getId());

        L3RouteVO vo = dbf.findByUuid(msg.getUuid(), L3RouteVO.class);
        L3EndPointVO l3EndPointVO = dbf.findByUuid(vo.getL3EndPointUuid(), L3EndPointVO.class);

        if(l3EndPointVO.getState() == L3EndpointState.Enabled){
            l3EndPointVO.setState(L3EndpointState.Deploying);
            l3EndPointVO = dbf.updateAndRefresh(l3EndPointVO);

            new L3NetworkTaskBase().taskDeleteL3EndpointRoutes(vo.getUuid());
        }else{
            dbf.remove(vo);
        }

        evt.setInventory(L3EndPointInventory.valueOf(l3EndPointVO));
        bus.publish(evt);
    }

    private void handle(APIEnableL3EndPointMsg msg){
        APIEnableL3EndPointEvent evt = new APIEnableL3EndPointEvent(msg.getId());

        L3NetworkTaskBase l3NetworkTaskBase = new L3NetworkTaskBase();

        L3EndPointVO vo = dbf.findByUuid(msg.getUuid(), L3EndPointVO.class);

        if(msg.isSaveOnly()){

            vo.setState(L3EndpointState.Enabled);
            vo.setStatus(L3EndpointStatus.Connected);
            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(L3EndPointInventory.valueOf(vo));

            bus.publish(evt);
        }else{

            l3NetworkTaskBase.taskEnableL3EndPoint(vo, new ReturnValueCompletion<L3EndPointInventory>(null) {
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
    }

    private void handle(APIDisableL3EndPointMsg msg){
        APIDisableL3EndPointEvent evt = new APIDisableL3EndPointEvent(msg.getId());

        L3NetworkTaskBase l3NetworkTaskBase = new L3NetworkTaskBase();

        L3EndPointVO vo = dbf.findByUuid(msg.getUuid(), L3EndPointVO.class);

        if(msg.isSaveOnly()){

            vo.setState(L3EndpointState.Disabled);
            vo.setStatus(L3EndpointStatus.Disconnected);
            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(L3EndPointInventory.valueOf(vo));

            bus.publish(evt);
        }else{
            vo.setState(L3EndpointState.Deploying);
            vo = dbf.updateAndRefresh(vo);
            l3NetworkTaskBase.taskDisableL3EndPoint(vo, new ReturnValueCompletion<L3EndPointInventory>(null) {
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
        }else if(msg instanceof APICreateL3NetworkManualMsg){
            base.validate((APICreateL3NetworkManualMsg) msg);
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
        }else if(msg instanceof APICreateL3EndPointManualMsg){
            base.validate((APICreateL3EndPointManualMsg) msg);
        }else if(msg instanceof APIEnableL3EndPointMsg){
            base.validate((APIEnableL3EndPointMsg) msg);
        }else if(msg instanceof APIDisableL3EndPointMsg){
            base.validate((APIDisableL3EndPointMsg) msg);
        }

        return msg;
    }
}
