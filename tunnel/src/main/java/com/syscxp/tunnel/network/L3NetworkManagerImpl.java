package com.syscxp.tunnel.network;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.keyvalue.Op;
import com.syscxp.header.AbstractService;
import com.syscxp.header.message.Message;
import com.syscxp.header.tunnel.network.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;


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
        vo.setVid(vid_q.getSingleResult()==null ? 100000L : vid_q.getSingleResult());
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
