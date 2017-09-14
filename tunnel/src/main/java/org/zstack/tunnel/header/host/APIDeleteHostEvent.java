package org.zstack.tunnel.header.host;

import org.zstack.header.message.APIEvent;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-14.
 * @Description: .
 */
public class APIDeleteHostEvent extends APIEvent {
    public APIDeleteHostEvent() {
    }

    public APIDeleteHostEvent(String apiId) {
        super(apiId);
    }
}
