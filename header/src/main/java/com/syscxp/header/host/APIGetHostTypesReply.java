package com.syscxp.header.host;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

public class APIGetHostTypesReply extends APIReply {
    private List<String> hostTypes;

    public List<String> getHostTypes() {
        return hostTypes;
    }

    public void setHostTypes(List<String> hostTypes) {
        this.hostTypes = hostTypes;
    }
 
    public static APIGetHostTypesReply __example__() {
        APIGetHostTypesReply reply = new APIGetHostTypesReply();


        return reply;
    }

}
