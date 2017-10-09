package com.syscxp.portal.apimediator;

import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.message.APIMessage;

/**
 * Created with IntelliJ IDEA.
 * User: frank
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ApiMessageProcessor {
    APIMessage process(APIMessage msg) throws ApiMessageInterceptionException;

    ApiMessageDescriptor getApiMessageDescriptor(APIMessage msg);
}
