package com.syscxp.core.externalusage;


import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@InnerCredentialCheck
public class APICreateExternalUsageMsg extends APISyncCallMessage {
    @APIParam
    private String resourceUuid;
    @APIParam
    private String resourceType;

    @APIParam
    private String usedFor;
    @APIParam
    private String usedForResourceUuid;
    @APIParam
    private String usedForResourceType;

    @APIParam
    private String deleteCascadeType;


}
