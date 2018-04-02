package com.syscxp.tunnel.network;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.configuration.MotifyType;
import com.syscxp.header.configuration.ResourceMotifyRecordVO;
import com.syscxp.header.configuration.ResourceMotifyRecordVO_;
import com.syscxp.header.core.ReturnValueCompletion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.tunnel.L3NetWorkConstant;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.header.tunnel.tunnel.InterfaceVO;
import com.syscxp.tunnel.tunnel.TunnelBase;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        }else if(msg instanceof APICreateL3EndpointMsg){
            handle((APICreateL3EndpointMsg) msg);
        }else if(msg instanceof APICreateL3EndpointManualMsg){
            handle((APICreateL3EndpointManualMsg) msg);
        }else if(msg instanceof APIUpdateL3EndpointIPMsg){
            handle((APIUpdateL3EndpointIPMsg) msg);
        }else if(msg instanceof APIUpdateL3EndpointBandwidthMsg){
            handle((APIUpdateL3EndpointBandwidthMsg) msg);
        }else if(msg instanceof APIDeleteL3EndpointMsg){
            handle((APIDeleteL3EndpointMsg) msg);
        }else if(msg instanceof APICreateL3RouteMsg){
            handle((APICreateL3RouteMsg) msg);
        }else if(msg instanceof APIDeleteL3RouteMsg){
            handle((APIDeleteL3RouteMsg) msg);
        }else if(msg instanceof APIEnableL3EndpointMsg){
            handle((APIEnableL3EndpointMsg) msg);
        }else if(msg instanceof APIDisableL3EndpointMsg){
            handle((APIDisableL3EndpointMsg) msg);
        }else if(msg instanceof APIGetModifyBandwidthNumMsg){
            handle((APIGetModifyBandwidthNumMsg) msg);
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
        vo.setEndpointNum(0);
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
        vo.setEndpointNum(0);
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

    private void handle(APICreateL3EndpointMsg msg) {
        APICreateL3EndpointEvent evt = new APICreateL3EndpointEvent(msg.getId());

        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        String l3EndpointUuid = doCreateL3EndpointVO(msg);
        L3EndpointVO vo = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);

        l3NetworkBase.updateEndpointNum(msg.getL3NetworkUuid());

        evt.setInventory(L3EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateL3EndpointManualMsg msg){
        APICreateL3EndpointManualEvent evt = new APICreateL3EndpointManualEvent(msg.getId());

        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        String l3EndpointUuid = doCreateL3EndpointManualVO(msg);
        L3EndpointVO vo = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);

        l3NetworkBase.updateEndpointNum(msg.getL3NetworkUuid());

        evt.setInventory(L3EndpointInventory.valueOf(vo));
        bus.publish(evt);

    }

    @Transactional
    private String doCreateL3EndpointVO(APICreateL3EndpointMsg msg){
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

        L3EndpointVO vo = new L3EndpointVO();
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
        vo.setMonitorIp(null);
        vo.setNetmask(null);
        vo.setIpCidr(null);
        vo.setInterfaceUuid(msg.getInterfaceUuid());
        vo.setSwitchPortUuid(switchPortUuid);
        vo.setPhysicalSwitchUuid(physicalSwitchUuid);
        vo.setVlan(vlan);
        vo.setRd("58991:"+Integer.toString(l3NetworkVO.getVid()));

        L3RtVO rtVO = new L3RtVO();
        rtVO.setUuid(Platform.getUuid());
        rtVO.setL3EndpointUuid(vo.getUuid());
        rtVO.setImpor("58991:"+Integer.toString(l3NetworkVO.getVid()));
        rtVO.setExport("58991:"+Integer.toString(l3NetworkVO.getVid()));

        dbf.getEntityManager().persist(vo);
        dbf.getEntityManager().persist(rtVO);

        return vo.getUuid();
    }

    @Transactional
    private String doCreateL3EndpointManualVO(APICreateL3EndpointManualMsg msg){
        TunnelBase tunnelBase = new TunnelBase();

        BandwidthOfferingVO bandwidthOfferingVO = dbf.findByUuid(msg.getBandwidthOfferingUuid(), BandwidthOfferingVO.class);
        L3NetworkVO l3NetworkVO = dbf.findByUuid(msg.getL3NetworkUuid(), L3NetworkVO.class);
        InterfaceVO interfaceVO = dbf.findByUuid(msg.getInterfaceUuid(), InterfaceVO.class);

        String switchPortUuid = interfaceVO.getSwitchPortUuid();
        String physicalSwitchUuid = tunnelBase.getPhysicalSwitchBySwitchPortUuid(interfaceVO.getSwitchPortUuid()).getUuid();

        L3EndpointVO vo = new L3EndpointVO();
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
        vo.setMonitorIp(null);
        vo.setNetmask(null);
        vo.setIpCidr(null);
        vo.setInterfaceUuid(msg.getInterfaceUuid());
        vo.setSwitchPortUuid(switchPortUuid);
        vo.setPhysicalSwitchUuid(physicalSwitchUuid);
        vo.setVlan(msg.getVlan());
        vo.setRd("58991:"+Integer.toString(l3NetworkVO.getVid()));

        L3RtVO rtVO = new L3RtVO();
        rtVO.setUuid(Platform.getUuid());
        rtVO.setL3EndpointUuid(vo.getUuid());
        rtVO.setImpor("58991:"+Integer.toString(l3NetworkVO.getVid()));
        rtVO.setExport("58991:"+Integer.toString(l3NetworkVO.getVid()));

        dbf.getEntityManager().persist(vo);
        dbf.getEntityManager().persist(rtVO);

        return vo.getUuid();
    }

    private void handle(APIUpdateL3EndpointIPMsg msg){
        APIUpdateL3EndpointIPEvent evt = new APIUpdateL3EndpointIPEvent(msg.getId());

        String l3EndpointUuid = doUpdateL3EndpointIP(msg);
        L3EndpointVO vo = dbf.findByUuid(l3EndpointUuid, L3EndpointVO.class);

        evt.setInventory(L3EndpointInventory.valueOf(vo));
        bus.publish(evt);

    }

    @Transactional
    private String doUpdateL3EndpointIP(APIUpdateL3EndpointIPMsg msg){
        L3EndpointVO vo = dbf.findByUuid(msg.getUuid(), L3EndpointVO.class);

        vo.setRemoteIp(msg.getRemoteIp());
        vo.setLocalIP(msg.getLocalIP());
        vo.setNetmask(msg.getNetmask());
        vo.setMonitorIp(msg.getMonitorIp());
        vo.setIpCidr(NetworkUtils.getIpCidrFromIpv4Netmask(msg.getLocalIP(), msg.getNetmask()));

        dbf.getEntityManager().merge(vo);

        if(Q.New(L3RouteVO.class).eq(L3RouteVO_.l3EndpointUuid, msg.getUuid()).isExists()){
            List<L3RouteVO> l3RouteVOs = Q.New(L3RouteVO.class).eq(L3RouteVO_.l3EndpointUuid, msg.getUuid()).list();
            for(L3RouteVO l3RouteVO : l3RouteVOs){
                l3RouteVO.setNextIp(msg.getRemoteIp());
                dbf.getEntityManager().merge(l3RouteVO);
            }
        }

        return msg.getUuid();
    }

    private void handle(APIUpdateL3EndpointBandwidthMsg msg){
        APIUpdateL3EndpointBandwidthEvent evt = new APIUpdateL3EndpointBandwidthEvent(msg.getId());

        L3EndpointVO vo = dbf.findByUuid(msg.getUuid(), L3EndpointVO.class);

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

        evt.setInventory(L3EndpointInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteL3EndpointMsg msg) {
        APIDeleteL3EndpointEvent evt = new APIDeleteL3EndpointEvent(msg.getId());

        L3EndpointVO vo = dbf.findByUuid(msg.getUuid(), L3EndpointVO.class);

        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        l3NetworkBase.deleteL3EndpointDB(msg.getUuid());
        l3NetworkBase.updateEndpointNum(vo.getL3NetworkUuid());

        bus.publish(evt);

    }

    private void handle(APICreateL3RouteMsg msg) {
        APICreateL3RouteEvent evt = new APICreateL3RouteEvent(msg.getId());
        L3NetworkBase l3NetworkBase = new L3NetworkBase();

        L3EndpointVO l3EndpointVO = dbf.findByUuid(msg.getL3EndpointUuid(), L3EndpointVO.class);

        Integer index = l3NetworkBase.getIndexForRoute(msg.getL3EndpointUuid());

        String[] cidrArr = msg.getCidr().split("/");
        String truthCidr = NetworkUtils.getIpCidrFromIpv4Netmask(cidrArr[0],NetworkUtils.netmaskFromInt(cidrArr[1]));

        L3RouteVO vo = new L3RouteVO();
        vo.setUuid(Platform.getUuid());
        vo.setL3EndpointUuid(msg.getL3EndpointUuid());
        vo.setCidr(msg.getCidr());
        vo.setTruthCidr(truthCidr);
        vo.setNextIp(l3EndpointVO.getRemoteIp());
        vo.setIndexNum(index);

        vo = dbf.persistAndRefresh(vo);

        if(l3EndpointVO.getState() == L3EndpointState.Enabled){

            new L3NetworkTaskBase().taskAddL3EndpointRoutes(vo, new ReturnValueCompletion<L3RouteInventory>(null) {
                @Override
                public void success(L3RouteInventory inv) {
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
            evt.setInventory(L3RouteInventory.valueOf(vo));
            bus.publish(evt);
        }


    }

    private void handle(APIDeleteL3RouteMsg msg) {
        APIDeleteL3RouteEvent evt = new APIDeleteL3RouteEvent(msg.getId());

        L3RouteVO vo = dbf.findByUuid(msg.getUuid(), L3RouteVO.class);
        L3EndpointVO l3EndpointVO = dbf.findByUuid(vo.getL3EndpointUuid(), L3EndpointVO.class);

        if(l3EndpointVO.getState() == L3EndpointState.Enabled){
            l3EndpointVO.setState(L3EndpointState.Deploying);
            dbf.updateAndRefresh(l3EndpointVO);

            new L3NetworkTaskBase().taskDeleteL3EndpointRoutes(vo.getUuid());
        }else{
            dbf.remove(vo);
        }

        bus.publish(evt);
    }

    private void handle(APIEnableL3EndpointMsg msg){
        APIEnableL3EndpointEvent evt = new APIEnableL3EndpointEvent(msg.getId());

        L3NetworkTaskBase l3NetworkTaskBase = new L3NetworkTaskBase();

        L3EndpointVO vo = dbf.findByUuid(msg.getUuid(), L3EndpointVO.class);

        if(msg.isSaveOnly()){

            vo.setState(L3EndpointState.Enabled);
            vo.setStatus(L3EndpointStatus.Connected);
            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(L3EndpointInventory.valueOf(vo));

            bus.publish(evt);
        }else{

            l3NetworkTaskBase.taskEnableL3Endpoint(vo, new ReturnValueCompletion<L3EndpointInventory>(null) {
                @Override
                public void success(L3EndpointInventory inv) {
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

    private void handle(APIDisableL3EndpointMsg msg){
        APIDisableL3EndpointEvent evt = new APIDisableL3EndpointEvent(msg.getId());

        L3NetworkTaskBase l3NetworkTaskBase = new L3NetworkTaskBase();

        L3EndpointVO vo = dbf.findByUuid(msg.getUuid(), L3EndpointVO.class);

        if(msg.isSaveOnly()){

            vo.setState(L3EndpointState.Disabled);
            vo.setStatus(L3EndpointStatus.Disconnected);
            vo = dbf.updateAndRefresh(vo);

            evt.setInventory(L3EndpointInventory.valueOf(vo));

            bus.publish(evt);
        }else{
            vo.setState(L3EndpointState.Deploying);
            vo = dbf.updateAndRefresh(vo);
            l3NetworkTaskBase.taskDisableL3Endpoint(vo, new ReturnValueCompletion<L3EndpointInventory>(null) {
                @Override
                public void success(L3EndpointInventory inv) {
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

    /**
     * 调整带宽的次数查询
     */
    private void handle(APIGetModifyBandwidthNumMsg msg) {
        LocalDateTime dateTime =
                LocalDate.now().withDayOfMonth(LocalDate.MIN.getDayOfMonth()).atTime(LocalTime.MIN);
        Long times = Q.New(ResourceMotifyRecordVO.class).eq(ResourceMotifyRecordVO_.resourceUuid, msg.getUuid())
                .gte(ResourceMotifyRecordVO_.createDate, Timestamp.valueOf(dateTime)).count();
        Integer maxModifies =
                Q.New(L3NetworkVO.class).eq(L3NetworkVO_.uuid, msg.getUuid()).select(L3NetworkVO_.maxModifies)
                        .findValue();
        APIGetModifyBandwidthNumReply reply = new APIGetModifyBandwidthNumReply();
        reply.setMaxModifies(maxModifies);
        reply.setHasModifies(Math.toIntExact(times));
        reply.setLeftModifies((int) (maxModifies - times));

        bus.reply(msg, reply);
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
        }else if(msg instanceof APICreateL3EndpointMsg){
            base.validate((APICreateL3EndpointMsg) msg);
        }else if(msg instanceof APIUpdateL3EndpointIPMsg){
            base.validate((APIUpdateL3EndpointIPMsg) msg);
        }else if(msg instanceof APIUpdateL3EndpointBandwidthMsg){
            base.validate((APIUpdateL3EndpointBandwidthMsg) msg);
        }else if(msg instanceof APIDeleteL3EndpointMsg){
            base.validate((APIDeleteL3EndpointMsg) msg);
        }else if(msg instanceof APICreateL3RouteMsg){
            base.validate((APICreateL3RouteMsg) msg);
        }else if(msg instanceof APIDeleteL3RouteMsg){
            base.validate((APIDeleteL3RouteMsg) msg);
        }else if(msg instanceof APICreateL3EndpointManualMsg){
            base.validate((APICreateL3EndpointManualMsg) msg);
        }else if(msg instanceof APIEnableL3EndpointMsg){
            base.validate((APIEnableL3EndpointMsg) msg);
        }else if(msg instanceof APIDisableL3EndpointMsg){
            base.validate((APIDisableL3EndpointMsg) msg);
        }

        return msg;
    }
}
