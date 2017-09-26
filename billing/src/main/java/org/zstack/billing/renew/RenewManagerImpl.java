package org.zstack.billing.renew;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.billing.header.renew.*;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.billing.*;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.header.rest.RESTFacade;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

public class RenewManagerImpl  extends AbstractService implements  ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(RenewManagerImpl.class);

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
        if (msg instanceof APIUpdateRenewMsg) {
            handle((APIUpdateRenewMsg) msg);
        }  else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIUpdateRenewMsg msg) {
        RenewVO vo = dbf.findByUuid(msg.getUuid(), RenewVO.class);
        if (vo.isRenewAuto() != msg.isRenewAuto()) {
            vo.setRenewAuto(msg.isRenewAuto());
        }
        dbf.updateAndRefresh(vo);
        RenewInventory ri = RenewInventory.valueOf(vo);
        APIUpdateRenewEvent evt = new APIUpdateRenewEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {
        try {

        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
        return true;
    }


    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {

        if (msg instanceof APICreateOrderMsg) {
            validate((APICreateOrderMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateOrderMsg msg) {

    }

}
