package com.syscxp.query;

import java.util.List;

/**
 */
public abstract class AbstractMysqlQuerySubQueryExtension implements MysqlQuerySubQueryExtension {
    @Override
    public List<String> getEscapeConditionNames() {
        return null;
    }
}
