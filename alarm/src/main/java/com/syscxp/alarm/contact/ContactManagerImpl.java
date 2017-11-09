package com.syscxp.alarm.contact;

import com.syscxp.alarm.header.contact.*;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ContactManagerImpl  extends AbstractService implements ApiMessageInterceptor {


    private static final CLogger logger = Utils.getLogger(ContactManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
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
        if (msg instanceof APICreateContactMsg) {
            handle((APICreateContactMsg) msg);
        }else if(msg instanceof  APIDeleteContactMsg){
            handle((APIDeleteContactMsg) msg);
        }else if(msg instanceof  APIUpdateContactMsg){
            handle((APIUpdateContactMsg) msg);
        }else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    @Transactional
    private void handle(APIUpdateContactMsg msg) {

        String uuid = msg.getUuid();
        ContactVO vo = dbf.getEntityManager().find(ContactVO.class, uuid);
        if(vo!=null){
            if(msg.getEmail()!=null){
                vo.setEmail(msg.getEmail());
            }
            if(msg.getName()!=null){
                vo.setName(msg.getName());
            }
            if(msg.getMobile()!=null){
                vo.setMobile(msg.getMobile());
            }
            dbf.getEntityManager().merge(vo);
            dbf.getEntityManager().flush();
            dbf.getEntityManager().refresh(vo);
            UpdateQuery q = UpdateQuery.New(ContactNotifyWayRefVO.class);
            q.condAnd(ContactNotifyWayRefVO_.contactUuid, SimpleQuery.Op.EQ, vo.getUuid());
            q.delete();
            List<String> codes = msg.getWays();
            if(codes == null || codes.size()==0){
                persistNotifyWay(vo.getUuid(),"mobile");
            }else {
                for(String code: codes){
                    persistNotifyWay(vo.getUuid(),code);
                }
            }
            dbf.getEntityManager().flush();
        }
        APIUpdateContactEvent event = new APIUpdateContactEvent(msg.getId());
        event.setInventory(ContactInventory.valueOf(dbf.findByUuid(msg.getUuid(),ContactVO.class)));
        bus.publish(event);
    }

    @Transactional
    private void handle(APIDeleteContactMsg msg) {
        String uuid =  msg.getUuid();
        ContactVO vo = dbf.findByUuid(uuid, ContactVO.class);
        if(vo != null){
            dbf.remove(vo);
//            dbf.getEntityManager().remove(vo);
//            UpdateQuery q = UpdateQuery.New(ContactNotifyWayRefVO.class);
//            q.condAnd(ContactNotifyWayRefVO_.contactUuid, SimpleQuery.Op.EQ, vo.getUuid());
//            q.delete();
        }
        APIDeleteContactEvent event = new APIDeleteContactEvent(msg.getId());
        event.setInventory(ContactInventory.valueOf(vo));
        bus.publish(event);
    }


    @Transactional
    private void handle(APICreateContactMsg msg) {
        List<String> codes = msg.getWays();
        ContactVO vo = new ContactVO();
        vo.setUuid(Platform.getUuid());
        vo.setEmail(msg.getEmail());
        vo.setMobile(msg.getMobile());
        vo.setName(msg.getName());
        vo.setAccountUuid(msg.getAccountUuid());

        dbf.getEntityManager().persist(vo);

        if(codes == null || codes.size()==0){
            persistNotifyWay(vo.getUuid(),"mobile");
        }else {
            for(String code: codes){
                persistNotifyWay(vo.getUuid(),code);
            }
        }

        dbf.getEntityManager().flush();
        APICreateContactEvent evt = new APICreateContactEvent(msg.getId());
        evt.setInventory(ContactInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    private void persistNotifyWay(String contactUuid,String code){
        SimpleQuery<NotifyWayVO> q = dbf.createQuery(NotifyWayVO.class);
        q.add(NotifyWayVO_.code, SimpleQuery.Op.EQ,code);
        NotifyWayVO notifyWayVO = q.find();
        ContactNotifyWayRefVO contactNotifyWayRefVO = new ContactNotifyWayRefVO();
        contactNotifyWayRefVO.setContactUuid(contactUuid);
        contactNotifyWayRefVO.setNotifyWayUuid(notifyWayVO.getUuid());
        contactNotifyWayRefVO.setUuid(Platform.getUuid());
        dbf.getEntityManager().persist(contactNotifyWayRefVO);
    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(AlarmConstant.SERVICE_ID_CONTACT);
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
