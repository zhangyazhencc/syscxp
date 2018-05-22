package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.message.APIEvent;

public class APIDeleteCloudHubEvent extends APIEvent {

    public APIDeleteCloudHubEvent(){}

    public APIDeleteCloudHubEvent(String apiId) {
        super(apiId);
    }

}
