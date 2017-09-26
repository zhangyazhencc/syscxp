package org.zstack.account.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.account.header.account.*;
import org.zstack.account.header.identity.*;
import org.zstack.account.header.ticket.*;
import org.zstack.account.header.user.*;
import org.zstack.account.identity.AccountBase;
import org.zstack.account.identity.AccountManager;
import org.zstack.account.identity.IdentiyInterceptor;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.account.APIValidateAccountMsg;
import org.zstack.header.account.APIValidateAccountReply;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.errorcode.SysErrors;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.*;
import org.zstack.header.managementnode.PrepareDbInitialValueExtensionPoint;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.header.query.QueryOp;
import org.zstack.sms.MailService;
import org.zstack.sms.SmsService;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.zstack.core.Platform.argerr;
import static org.zstack.core.Platform.operr;

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
        if (msg instanceof TicketMessage) {
            TicketBase base = new TicketBase();
            base.handleMessage(msg);
        } else if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg) {

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
            volidata((APICreateTicketMsg)msg);

        }
        setServiceId(msg);

        return msg;
    }

    private void volidata(APICreateTicketMsg msg) {
        SimpleQuery<DictionaryVO> q = dbf.createQuery(DictionaryVO.class);
        q.add(DictionaryVO_.dictKey, Op.EQ, "TicketType");
        List<DictionaryVO> list = q.list();
        for(DictionaryVO vo : list){
            if(vo.getValueName().equals(msg.getType())){

            }

        }

        msg.getType();
    }

    private void setServiceId(APIMessage msg) {
        if (msg instanceof TicketMessage) {
            TicketMessage amsg = (TicketMessage) msg;
            bus.makeTargetServiceIdByResourceUuid(msg, AccountConstant.SERVICE_ID, amsg.getAccountUuid());
        }
    }

}
