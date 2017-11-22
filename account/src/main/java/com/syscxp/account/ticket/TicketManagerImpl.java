package com.syscxp.account.ticket;

import com.syscxp.account.header.identity.SessionVO;
import com.syscxp.account.header.ticket.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.account.identity.IdentiyInterceptor;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.List;

import static com.syscxp.core.Platform.argerr;

/**
 * Created by wangwg on 2017/09/25.
 */
public class TicketManagerImpl extends AbstractService implements TicketManager,
        ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(TicketManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private IdentiyInterceptor identiyInterceptor;

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
       if (msg instanceof APIMessage) {
           handleApiMessage((APIMessage) msg);
       } else {
           bus.dealWithUnknownMessage(msg);
       }
    }

    private void handleApiMessage(APIMessage msg) {
        if(msg instanceof APICreateTicketRecordMsg){
            handle((APICreateTicketRecordMsg)msg);
        }else if(msg instanceof APICreateTicketMsg){
            handle((APICreateTicketMsg)msg);
        }else if(msg instanceof APIDeleteTicketMsg){
            handle((APIDeleteTicketMsg)msg);
        }else if(msg instanceof APIUpdateTicketMsg){
            handle((APIUpdateTicketMsg)msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APIUpdateTicketMsg msg) {

        TicketVO vo = dbf.findByUuid(msg.getUuid(),TicketVO.class);
        boolean isupdate = false;
        if(msg.getContent() != null){
            vo.setContent(msg.getContent());
            isupdate = true;
        }
        if(msg.getEmail() != null){
            vo.setEmail(msg.getEmail());
            isupdate = true;
        }
        if(msg.getPhone() != null){
            vo.setPhone(msg.getPhone());
            isupdate = true;
        }
        if(msg.getStatus() != null){
            vo.setStatus(msg.getStatus());
            isupdate = true;
        }
        if(msg.getAdminUserUuid() != null){
            vo.setAdminUserUuid(msg.getAdminUserUuid());
            isupdate = true;
        }

        if(msg.getTicketTypeCode() != null){
            List<TicketTypeVO> list =  dbf.createQuery(TicketTypeVO.class).list();
            boolean is = false;
            for(TicketTypeVO tyvo : list){
                if(tyvo.getCode().equals(msg.getTicketTypeCode())){
                    is = true;
                }
            }
            if(!is){
                throw new ApiMessageInterceptionException(argerr("value[%s] of type is not exist",
                        msg.getTicketTypeCode()));
            }
            vo.setTicketTypeCode(msg.getTicketTypeCode());
            isupdate = true;
        }
        if(isupdate){
           vo = dbf.updateAndRefresh(vo);
        }

        APIUpdateTicketEvent event = new APIUpdateTicketEvent(msg.getId());

        event.setInventory(TicketInventory.valueOf(vo));
        bus.publish(event);

    }

    private void handle(APIDeleteTicketMsg msg) {

        dbf.removeByPrimaryKey(msg.getUuid(), TicketVO.class);
        APIDeleteTicketEvent evt = new APIDeleteTicketEvent(msg.getId());

        bus.publish(evt);
    }


    @Transactional
    private void handle(APICreateTicketRecordMsg msg) {
        TicketRecordVO vo = new TicketRecordVO();
        vo.setUuid(Platform.getUuid());
        vo.setTicketUuid(msg.getTicketUuid());
        vo.setRecordBy(msg.getRecordBy());
        vo.setContent(msg.getContent());
        vo.setStatus(msg.getStatus());
        vo.setAccountUuid(msg.getSession().getAccountUuid());
        vo.setUserUuid(msg.getSession().getUserUuid());

        vo = dbf.persistAndRefresh(vo);

        TicketVO tvo = dbf.findByUuid(msg.getTicketUuid(),TicketVO.class);
        tvo.setStatus(msg.getStatus());
        if(msg.getSession() != null && vo.getRecordBy() == RecordBy.AdminUser){
            tvo.setAdminUserUuid(msg.getSession().getUserUuid());
        }

        dbf.getEntityManager().merge(tvo);

        APICreateTicketRecordEvent event = new APICreateTicketRecordEvent(msg.getId());
        event.setInventory(TicketRecordInventory.valueOf(vo));
        bus.publish(event);

    }

    private void handle(APICreateTicketMsg msg) {

        TicketVO vo =  new TicketVO();
        vo.setUuid(Platform.getUuid());
        if(!msg.getTicketFrom().toString().equals(TicketFrom.console.toString())){
            if(msg.getSession().getUuid() == null){
                throw new ApiMessageInterceptionException(argerr("uuid of session is null"));
            }
            SessionVO svo = dbf.findByUuid(msg.getSession().getUuid(), SessionVO.class);
            if(svo==null){
                throw new ApiMessageInterceptionException(argerr("not login"));
            }
            vo.setAccountUuid(svo.getAccountUuid());
            if(!svo.getAccountUuid().equals(svo.getUserUuid())){
                vo.setUserUuid(svo.getUserUuid());
            }

        }
        vo.setTicketTypeCode(msg.getTicketTypeCode());

        if(msg.getContentExtra() != null){
            vo.setContentExtra(msg.getContentExtra());
        }

        vo.setEmail(msg.getEmail());
        vo.setPhone(msg.getPhone());

        vo.setTicketFrom(msg.getTicketFrom());
        vo.setContent(msg.getContent());
        vo.setStatus(TicketStatus.untreated);
        APICreateTicketEvent evt = new APICreateTicketEvent(msg.getId());
        evt.setInventory(TicketInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(evt);

    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(TicketConstant.SERVICE_ID);
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

        if (msg instanceof APICreateTicketMsg) {
            validate((APICreateTicketMsg)msg);

        }
        return msg;
    }

    private void validate(APICreateTicketMsg msg) {
        List<TicketTypeVO> list =  dbf.createQuery(TicketTypeVO.class).list();

        boolean is = false;
        for(TicketTypeVO vo : list){
            if(vo.getCode().equals(msg.getTicketTypeCode())){
                is = true;
            }
        }
        if(!is){
            throw new ApiMessageInterceptionException(argerr("value[%s] of type is not exist",
                    msg.getTicketTypeCode()));
        }

    }

}
