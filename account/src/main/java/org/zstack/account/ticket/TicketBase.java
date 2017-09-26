package org.zstack.account.ticket;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.zstack.account.header.account.APICreateAccountEvent;
import org.zstack.account.header.account.AccountInventory;
import org.zstack.account.header.ticket.*;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.identity.AbstractAccount;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;


@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TicketBase extends AbstractAccount {
    private static final CLogger logger = Utils.getLogger(TicketBase.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

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
        if (msg instanceof APICreateTicketMsg) {
            handle((APICreateTicketMsg)msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }


    }

    private void handle(APICreateTicketMsg msg) {

        TicketVO vo =  new TicketVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getSession().getAccountUuid());
        vo.setUserUuid(msg.getSession().getUserUuid());
        vo.setType(msg.getType());
        vo.setContent(msg.getContent());
        vo.setStatus(TicketStatus.untreated);

        APICreateTicketEvent evt = new APICreateTicketEvent(msg.getId());
        evt.setInventory(TicketInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(evt);
    }


}
