package com.syscxp.tunnel.network;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.header.AbstractService;
import com.syscxp.header.message.Message;
import com.syscxp.header.tunnel.network.*;
import org.springframework.beans.factory.annotation.Autowired;


public class L3NetworkManagerImpl extends AbstractService implements L3NetworkManager{

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

    private VOAddAllOfMsg vOAddAllOfMsg = new VOAddAllOfMsg();

    @Override
    public void handleMessage(Message msg) {
        if(msg instanceof APICreateL3NetworkMsg){
            handle((APICreateL3NetworkMsg) msg);
        }else if(msg instanceof APIUpdateL3NetworkMsg){
            handle((APIUpdateL3NetworkMsg) msg);
        }else if(msg instanceof APIDeleteL3NetworkMsg){
            handle((APIDeleteL3NetworkMsg) msg);
        }
        else {
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APIDeleteL3NetworkMsg msg) {
        APIDeleteL3NetworkEvent event = new APIDeleteL3NetworkEvent(msg.getId());
        UpdateQuery.New(L3NetworkVO.class).condAnd(L3NetworkVO_.uuid, SimpleQuery.Op.EQ,msg.getUuid()).delete();
        bus.publish(event);
    }

    private void handle(APIUpdateL3NetworkMsg msg) {
        APIUpdateL3NetworkEvent event = new APIUpdateL3NetworkEvent(msg.getId());
        L3NetworkVO vo = dbf.findByUuid(msg.getUuid(),L3NetworkVO.class);
        vOAddAllOfMsg.addAll(msg,vo);

        event.setInventory(L3NetworkInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);
    }

    private void handle(APICreateL3NetworkMsg msg) {
        APICreateL3NetworkEvent event = new APICreateL3NetworkEvent();
        L3NetworkVO vo = new L3NetworkVO();
        vOAddAllOfMsg.addAll(msg,vo);
        vo.setUuid(Platform.getUuid());
        event.setInventory(L3NetworkInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);
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
