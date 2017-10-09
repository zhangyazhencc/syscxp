package com.syscxp.query;

import com.syscxp.header.query.APIQueryMessage;

import java.util.List;

public interface QueryFacade {
    <T> List<T> query(APIQueryMessage msg, Class<T> inventoryClass);

    long count(APIQueryMessage msg, Class inventoryClass);
}
