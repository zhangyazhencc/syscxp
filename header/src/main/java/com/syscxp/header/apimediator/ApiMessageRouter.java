package com.syscxp.header.apimediator;

import com.syscxp.header.message.Message;

public interface ApiMessageRouter {
    String generateTargetServiceId(Message msg) throws CloudNoRouteFoundException;
}
