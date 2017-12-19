package com.syscxp.account.ticket;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.identity.SessionVO;
import com.syscxp.account.header.ticket.*;
import com.syscxp.account.header.user.APICreateUserMsg;
import com.syscxp.account.quota.TicketQuotaOperator;
import com.syscxp.account.quota.UserQuotaOperator;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.quota.QuotaConstant;
import com.syscxp.header.quota.ReportQuotaExtensionPoint;
import com.syscxp.header.tunnel.tunnel.APICreateTunnelMsg;
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
import static com.syscxp.utils.CollectionDSL.list;

/**
 * Created by wangwg on 2017/09/25.
 */
public class TicketManagerImpl extends AbstractService implements TicketManager,
        ApiMessageInterceptor, ReportQuotaExtensionPoint {
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
        }else if(msg instanceof APICloseTicketMsg){
            handle((APICloseTicketMsg)msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APICloseTicketMsg msg) {

        TicketVO vo = dbf.findByUuid(msg.getUuid(),TicketVO.class);
        APICloseTicketEvent event = new APICloseTicketEvent(msg.getId());

        if(msg.getSession().getAccountUuid().equals(vo.getAccountUuid())){
            vo.setStatus(TicketStatus.resolved);
            event.setInventory(TicketInventory.valueOf(vo));
        }else{
            event.setError(Platform.argerr("this ticket is not belong to this account"));
        }

        bus.publish(event);

    }

    private void handle(APIDeleteTicketMsg msg) {

        UpdateQuery.New(TicketRecordVO.class).condAnd(TicketRecordVO_.ticketUuid,
                SimpleQuery.Op.EQ,msg.getUuid()).delete();

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
        tvo.setLastOpDate(dbf.getCurrentSqlTime());
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
        if(msg.getTicketFrom().toString().equals(TicketFrom.console.toString())){
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
        vo.setTicketTypeUuid(msg.getTicketTypeUuid());

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
            if(vo.getUuid().equals(msg.getTicketTypeUuid())){
                is = true;
            }
        }
        if(!is){
            throw new ApiMessageInterceptionException(argerr("value[%s] of type is not exist",
                    msg.getTicketTypeUuid()));
        }

    }

    @Override
    public List<Quota> reportQuota() {
        TicketQuotaOperator quotaOperator = new TicketQuotaOperator();
        // interface quota
        Quota quota = new Quota();
        quota.setOperator(quotaOperator);
        quota.addMessageNeedValidation(APICreateTicketMsg.class);
        quota.addMessageNeedValidation(APICreateTicketRecordMsg.class);

        Quota.QuotaPair p = new Quota.QuotaPair();
        p.setName(AccountConstant.QUOTA_TICKET_NUM);
        p.setValue(QuotaConstant.QUOTA_TICKET_NUM);
        quota.addPair(p);

        p = new Quota.QuotaPair();
        p.setName(AccountConstant.QUOTA_TICKET_RECORD_NUM);
        p.setValue(QuotaConstant.QUOTA_TICKET_RECORD_NUM);
        quota.addPair(p);

        p = new Quota.QuotaPair();
        p.setName(AccountConstant.QUOTA_TICKET_NO_SESSION_NUM);
        p.setValue(QuotaConstant.QUOTA_TICKET_NO_SESSION_NUM);
        quota.addPair(p);


        return list(quota);
    }
}
