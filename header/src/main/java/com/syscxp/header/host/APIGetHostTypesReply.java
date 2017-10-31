package com.syscxp.header.host;

import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "hostTypes")
public class APIGetHostTypesReply extends APIReply {
    private List<String> hostTypes;

    public List<String> getHypervisorTypes() {
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