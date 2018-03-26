package com.syscxp.trustee.trustee;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.message.Message;
import com.syscxp.header.tunnel.network.VOAddAllOfMsg;
import org.springframework.beans.factory.annotation.Autowired;

public class TrusteeManagerImpl extends AbstractService implements TrusteeManager {

    @Autowired
    private CloudBus bus;

    @Autowired
    private DatabaseFacade dbf;

    private VOAddAllOfMsg vOAddAllOfMsg = new VOAddAllOfMsg();

    @Override
    public void handleMessage(Message msg) {


    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(TrusteeConstant.SERVICE_ID);
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
