package com.syscxp.core.debug;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

/**
 * Created by xing5 on 2016/7/25.
 */
public class APIDebugSignalMsg extends APIMessage {
    @APIParam
    private List<String> signals;

    public List<String> getSignals() {
        return signals;
    }

    public void setSignals(List<String> signals) {
        this.signals = signals;
    }
 
    public static APIDebugSignalMsg __example__() {
        APIDebugSignalMsg msg = new APIDebugSignalMsg();


        return msg;
    }

}
