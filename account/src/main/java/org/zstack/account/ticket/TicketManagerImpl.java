package org.zstack.account.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.account.header.account.*;
import org.zstack.account.header.ticket.*;
import org.zstack.account.identity.IdentiyInterceptor;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.util.List;

import static org.zstack.core.Platform.argerr;

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
        }else{
            bus.dealWithUnknownMessage(msg);
        }

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
        vo.setBelongTo(msg.getBelongto());
        vo.setContent(msg.getContent());
        vo.setStatus(msg.getStatus());
        vo = dbf.persistAndRefresh(vo);

        TicketVO tvo = dbf.findByUuid(msg.getTicketUuid(),TicketVO.class);
        tvo.setStatus(msg.getStatus());
        dbf.getEntityManager().merge(tvo);

        APICreateTicketRecordEvent event = new APICreateTicketRecordEvent(msg.getId());
        event.setInventory(TicketRecordInventory.valueOf(vo));
        bus.publish(event);

    }

    private void handle(APICreateTicketMsg msg) {

        TicketVO vo =  new TicketVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getSession().getAccountUuid());
        if(!msg.getSession().getAccountUuid().equals(msg.getSession().getUserUuid())){
            vo.setUserUuid(msg.getSession().getUserUuid());
        }
        vo.setType(msg.getType());
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
        setServiceId(msg);

        return msg;
    }

    private void validate(APICreateTicketMsg msg) {
        List<TicketTypeVO> list =  dbf.createQuery(TicketTypeVO.class).list();
        boolean is = false;
        for(TicketTypeVO vo : list){
            if(vo.getTypeValue().equals(msg.getType())){
                is = true;
            }
        }
        if(!is){
            throw new ApiMessageInterceptionException(argerr("value[%s] of type is not exist",
                    msg.getType()));
        }

    }

    private void setServiceId(APIMessage msg) {
        if (msg instanceof TicketMessage) {
            TicketMessage amsg = (TicketMessage) msg;
            bus.makeTargetServiceIdByResourceUuid(msg, AccountConstant.SERVICE_ID, amsg.getAccountUuid());
        }
    }

}
