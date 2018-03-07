package com.syscxp.tunnel.network;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.header.AbstractService;
import com.syscxp.header.message.Message;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.tunnel.sdnController.L3NetworkCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;


public class L3NetworkManagerImpl extends AbstractService implements L3NetworkManager{

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

    private VOAddAllOfMsg vOAddAllOfMsg = new VOAddAllOfMsg();

    @Override
    public void handleMessage(Message msg) {
        if(msg instanceof APICreateL3NetworkMsg){
            handle((APICreateL3NetworkMsg) msg);
        }else if(msg instanceof APIUpdateL3NetworkMsg){
            handle((APIUpdateL3NetworkMsg) msg);
        }else if(msg instanceof APIDeleteL3NetworkMsg){
            handle((APIDeleteL3NetworkMsg) msg);
        }else if(msg instanceof APICreateL3EndPointMsg){
            handle((APICreateL3EndPointMsg) msg);
        }else if(msg instanceof APIUpdateL3EndPointMsg){
            handle((APIUpdateL3EndPointMsg) msg);
        }else if(msg instanceof APIDeleteL3EndPointMsg){
            handle((APIDeleteL3EndPointMsg) msg);
        }else if(msg instanceof APICreateL3RouteMsg){
            handle((APICreateL3RouteMsg) msg);
        }else if(msg instanceof APIUpdateL3RouteMsg){
            handle((APIUpdateL3RouteMsg) msg);
        }else if(msg instanceof APIDeleteL3RouteMsg){
            handle((APIDeleteL3RouteMsg) msg);
        }
        else {
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APIDeleteL3RouteMsg msg) {
        APIDeleteL3RouteEvent event = new APIDeleteL3RouteEvent(msg.getId());
        UpdateQuery.New(L3RouteVO.class).condAnd(L3RouteVO_.uuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
        bus.publish(event);
    }

    private void handle(APIUpdateL3RouteMsg msg) {
        APIUpdateL3RouteEvent event = new APIUpdateL3RouteEvent(msg.getId());
        L3RouteVO vo = dbf.findByUuid(msg.getUuid(),L3RouteVO.class);
        vOAddAllOfMsg.addAll(msg,vo);
        event.setInventory(L3RouteInventory.valueOf(dbf.updateAndRefresh(vo)));
        bus.publish(event);
    }

    private void handle(APICreateL3RouteMsg msg) {

        APICreateL3RouteEvent event = new APICreateL3RouteEvent(msg.getId());
        L3RouteVO vo = new L3RouteVO();
        vo.setUuid(Platform.getUuid());
        vOAddAllOfMsg.addAll(msg,vo);


        bus.publish(event);
    }

    @Transactional(propagation = Propagation.NESTED)
    private void handle(APIDeleteL3EndPointMsg msg) {
        APIDeleteL3EndPointEvent event = new APIDeleteL3EndPointEvent(msg.getId());
        UpdateQuery.New(L3EndPointVO.class).condAnd(L3EndPointVO_.uuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
        updateEndPointNum(msg.getUuid());
        bus.publish(event);
    }


    private void handle(APIUpdateL3EndPointMsg msg) {
        APIUpdateL3EndPointEvent event = new APIUpdateL3EndPointEvent(msg.getId());
        L3EndPointVO vo = dbf.findByUuid(msg.getUuid(),L3EndPointVO.class);
        vOAddAllOfMsg.addAll(msg,vo);
        event.setInventory(L3EndPointInventory.valueOf(dbf.updateAndRefresh(vo)));
        bus.publish(event);
    }

    @Transactional(propagation=Propagation.NESTED)
    private void handle(APICreateL3EndPointMsg msg) {
        APICreateL3EndPointEvent event = new APICreateL3EndPointEvent(msg.getId());
        L3EndPointVO vo = new L3EndPointVO();

        if(msg.getRouteType() == null){
            msg.setRouteType("STATIC");
        }
        if(msg.getStatus() == null){
            msg.setStatus("Connected");
        }
//        vo.setMaxRouteNum();
        TypedQuery<Long> vid_q = dbf.getEntityManager().createQuery(
                "SELECT MAX(vid) from L3NetworkEO where uuid = :l3Uuid", Long.class);
        vid_q.setParameter("l3Uuid",msg.getL3NetworkUuid());
        vo.setRd(String.valueOf(vid_q.getSingleResult()));

        vOAddAllOfMsg.addAll(msg,vo);
        vo.setUuid(Platform.getUuid());
        L3RtVO rtVo = new L3RtVO(
                Platform.getUuid(),
                msg.getEndpointUuid(),
                "100000","100000",
                dbf.getCurrentSqlTime(),dbf.getCurrentSqlTime()
        );

        dbf.getEntityManager().persist(vo);
        dbf.getEntityManager().persist(rtVo);
        updateEndPointNum(vo.getUuid());
        dbf.getEntityManager().flush();

        event.setInventory(L3EndPointInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIDeleteL3NetworkMsg msg) {
        APIDeleteL3NetworkEvent event = new APIDeleteL3NetworkEvent(msg.getId());
        UpdateQuery.New(L3NetworkVO.class).condAnd(L3NetworkVO_.uuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
        bus.publish(event);
    }

    private void handle(APIUpdateL3NetworkMsg msg) {
        APIUpdateL3NetworkEvent event = new APIUpdateL3NetworkEvent(msg.getId());
        L3NetworkVO vo = dbf.findByUuid(msg.getUuid(),L3NetworkVO.class);
        vOAddAllOfMsg.addAll(msg,vo);
        event.setInventory(L3NetworkInventory.valueOf(dbf.updateAndRefresh(vo)));
        bus.publish(event);
    }

    private void handle(APICreateL3NetworkMsg msg) {
        APICreateL3NetworkEvent event = new APICreateL3NetworkEvent();
        L3NetworkVO vo = new L3NetworkVO();
        vo.setUuid(Platform.getUuid());
        TypedQuery<Long> vid_q = dbf.getEntityManager().createQuery("SELECT MAX(vid) from L3NetworkVO ", Long.class);
        vo.setVid(vid_q.getSingleResult()==null ? 100000L : vid_q.getSingleResult() + 1L);
        vo.setEndPointNum(0L);
//        vo.setMaxModifies();
        if(msg.getType() == null){
            msg.setType("MPLSVPN");
        }
        if(msg.getStatus() == null){
            msg.setStatus("Connected");
        }

        vOAddAllOfMsg.addAll(msg,vo);

        event.setInventory(L3NetworkInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);
    }

    private void updateEndPointNum(String l3Networkuuid){
        TypedQuery<Long> num_q = dbf.getEntityManager().createQuery(
                "SELECT COUNT(*) from L3EndPointVO where  l3NetworkUuid = :l3Uuid ", Long.class);
        num_q.setParameter("l3Uuid",l3Networkuuid);

        UpdateQuery.New(L3NetworkVO.class).condAnd(L3NetworkVO_.uuid, SimpleQuery.Op.EQ,l3Networkuuid).
                set(L3NetworkVO_.endPointNum,num_q.getSingleResult()).update();

    }

    private L3NetworkCommand createL3NetworkCommand(String l3Uuid) {
        L3NetworkCommand cmd = new L3NetworkCommand();
        L3NetworkVO l3NetworkVO= dbf.findByUuid(l3Uuid,L3NetworkVO.class);
        cmd.setNet_id(l3NetworkVO.getUuid().substring(0,10));
        cmd.setUsername(l3NetworkVO.getOwnerAccountUuid());
        cmd.setVrf_id(l3NetworkVO.getVid());

        List<L3NetworkCommand.Mpls_switche> mpls_switcheList = new ArrayList<>();
        SimpleQuery<L3EndPointVO> l3Endpoint  = dbf.createQuery(L3EndPointVO.class);
        l3Endpoint.add(L3EndPointVO_.l3NetworkUuid, SimpleQuery.Op.EQ,l3NetworkVO.getUuid());
        List<L3EndPointVO> l3EndPointVOS = l3Endpoint.list();
        for(L3EndPointVO vo : l3EndPointVOS){
            L3NetworkCommand.Mpls_switche mpls_switche = cmd.new Mpls_switche();
            mpls_switche.setUuid(vo.getUuid());
            mpls_switche.setVlan_id(vo.getVlan());
            mpls_switche.setBandwidth(vo.getBandwidth());
            mpls_switche.setConnect_ip_local(vo.getLocalIP());
            mpls_switche.setConnect_ip_remote(vo.getRemoteIp());
            mpls_switche.setNetmask(vo.getNetmask());

            String physicalSwitchSql = "select phvo.username,phvo.password,phvo.mIP,phvo.protocol,phvo.port,spvo.portName,smvo.model,smvo.subModel" +
                    "from L3EndPointVO endvo,SwitchPortVO spvo,SwitchVO svo, PhysicalSwitchVO phvo,SwitchModelVO smvo" +
                    "where endvo.uuid = :endpointUuid and endvo.switchportUuid = spvo.uuid " +
                    "and spvo.switchuuid = svo.uuid and svo.PhysicalSwitchuuid = phvo.uuid and phvo.switchModelUuid = smvo.uuid;";
            TypedQuery<Tuple> physicalSwitchQuery = dbf.getEntityManager().createQuery(
                    "select username,password,mIP from PhysicalSwitchVO  where ", Tuple.class);
            physicalSwitchQuery.setParameter("endpointUuid", vo.getUuid());
            Tuple physicalSwitchT = physicalSwitchQuery.getSingleResult();

            mpls_switche.setUsername(physicalSwitchT.get(0,String.class));
            mpls_switche.setPassword(physicalSwitchT.get(1,String.class));
            mpls_switche.setM_ip(physicalSwitchT.get(2,String.class));
            mpls_switche.setProtocol(physicalSwitchT.get(3,String.class));
            mpls_switche.setPort(physicalSwitchT.get(4,String.class));
            mpls_switche.setPort_name(physicalSwitchT.get(5,String.class));
            mpls_switche.setSwitch_type(physicalSwitchT.get(6,String.class));
            mpls_switche.setSub_type(physicalSwitchT.get(7,String.class));

            List<L3NetworkCommand.Route> routeList = new ArrayList<>();
            SimpleQuery<L3RouteVO> l3RouteVOSimpleQuery  = dbf.createQuery(L3RouteVO.class);
            l3RouteVOSimpleQuery.add(L3RouteVO_.l3EndPointUuid, SimpleQuery.Op.EQ,vo.getUuid());
            List<L3RouteVO> l3RouteVOs = l3RouteVOSimpleQuery.list();
            for(L3RouteVO l3routeVO : l3RouteVOs){
                L3NetworkCommand.Route route = cmd.new Route();
                route.setIndex(l3routeVO.getIndex());
                String[] str = l3routeVO.getCidr().split("/");
                route.setBusiness_ip(str[0]);
                route.setNetmask(str[1]);
                route.setRoute_ip(l3routeVO.getNextIp());
                routeList.add(route);
            }

            mpls_switcheList.add(mpls_switche);
        }

        cmd.setMpls_switches(mpls_switcheList);

        return cmd;
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


}
