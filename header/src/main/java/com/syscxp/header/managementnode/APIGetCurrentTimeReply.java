package com.syscxp.header.managementnode;

import com.syscxp.header.message.APIReply;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mei Lei <meilei007@gmail.com> on 11/1/16.
 */
public class APIGetCurrentTimeReply extends APIReply {
    private Map<String, Long> currentTime;

    public Map<String, Long> getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Map<String, Long> currentTime) {
        this.currentTime = currentTime;
    }
 
    public static APIGetCurrentTimeReply __example__() {
        APIGetCurrentTimeReply reply = new APIGetCurrentTimeReply();
        Map<String, Long> ret = new HashMap<String, Long>();
        long currentTimeMillis = System.currentTimeMillis();
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        ret.put("MillionSeconds", currentTimeMillis);
        ret.put("Seconds", currentTimeSeconds);

        reply.setCurrentTime(ret);
        return reply;
    }

}
