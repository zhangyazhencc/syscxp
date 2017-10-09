package com.syscxp.query;

import com.syscxp.header.query.APIQueryMessage;

import java.util.List;

/**
 */
public interface MysqlQuerySubQueryExtension {
    String makeSubquery(APIQueryMessage msg, Class inventoryClass);

    List<String> getEscapeConditionNames();
}
