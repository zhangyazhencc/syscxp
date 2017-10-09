package com.syscxp.core.debug;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.header.AbstractService;
import com.syscxp.header.message.Message;

import java.util.List;

/**
 * Created by xing5 on 2016/7/25.
 */
public class DebugManagerImpl extends AbstractService implements DebugManager {
    @Autowired
    private CloudBus bus;

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIDebugSignalMsg) {
            handle((APIDebugSignalMsg)msg);
        }
    }

    private void handle(APIDebugSignalMsg msg) {
        APIDebugSignalEvent evt = new APIDebugSignalEvent(msg.getId());
        for (String sig : msg.getSignals()) {
            List<DebugSignalHandler> hs = sigHandlers.get(sig);
            if (hs == null) {
                continue;
            }

            for (DebugSignalHandler h : hs) {
                h.handleDebugSignal();
            }
        }

        bus.publish(evt);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(DebugConstant.SERVICE_ID);
    }
}
