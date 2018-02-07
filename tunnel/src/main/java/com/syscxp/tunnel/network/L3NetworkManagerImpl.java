package com.syscxp.tunnel.network;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.message.Message;
import com.syscxp.header.tunnel.network.APICreateL3NetworkMsg;
import com.syscxp.header.tunnel.network.APIDeleteL3NetworkMsg;
import com.syscxp.header.tunnel.network.APIUpdateL3NetworkMsg;
import com.syscxp.header.tunnel.network.L3NetWorkConstant;
import org.springframework.beans.factory.annotation.Autowired;

public class L3NetworkManagerImp extends AbstractService implements L3NetworkManager{

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

    @Override
    public void handleMessage(Message msg) {
        if(msg instanceof APICreateL3NetworkMsg){
            handle((APICreateL3NetworkMsg) msg);
        }else if(msg instanceof APIUpdateL3NetworkMsg){
            handle((APIUpdateL3NetworkMsg) msg);
        }else if(msg instanceof APIDeleteL3NetworkMsg){
            handle((APIDeleteL3NetworkMsg) msg);
        }


    }

    private void handle(APIDeleteL3NetworkMsg msg) {

    }

    private void handle(APIUpdateL3NetworkMsg msg) {
    }

    private void handle(APICreateL3NetworkMsg msg) {

    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(L3NetWorkConstant.SERVICE_ID);
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
