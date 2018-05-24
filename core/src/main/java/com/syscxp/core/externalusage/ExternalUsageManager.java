package com.syscxp.core.externalusage;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

public class ExternalUsageManager extends AbstractService {
    private CLogger logger = Utils.getLogger(getClass());

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

    @Override
    public void handleMessage(Message msg) {

    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(ExternalUsageConstant.SERVICE_ID);
    }

}
