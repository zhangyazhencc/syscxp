package com.syscxp.billing.report;

import com.syscxp.billing.header.report.APIGetDayExpenseReportMsg;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportManagerImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(ReportManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

    @Override
    public void handleMessage(Message msg) {

        if (msg instanceof APIGetDayExpenseReportMsg) {
            handle((APIGetDayExpenseReportMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APIGetDayExpenseReportMsg msg) {

    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID_REPORT);
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
