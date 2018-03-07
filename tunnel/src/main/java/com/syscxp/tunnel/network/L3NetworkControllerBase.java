package com.syscxp.tunnel.network;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Create by DCY on 2018/3/7
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class L3NetworkControllerBase {
    private static final CLogger logger = Utils.getLogger(L3NetworkControllerBase.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private RESTFacade restf;

    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg){

    }

    private void handleApiMessage(APIMessage msg){}
}
