package com.syscxp.core.externalusage;


import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

@InnerCredentialCheck
public class APIDeleteExternalUsageMsg extends APISyncCallMessage {
    @APIParam
    private String resourceUuid;
    @APIParam
    private String resourceType;

}
