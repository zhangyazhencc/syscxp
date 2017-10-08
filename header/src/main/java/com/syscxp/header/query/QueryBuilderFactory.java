package com.syscxp.header.query;

public interface QueryBuilderFactory {
    QueryBuilderType getQueryBuilderType();

    QueryBuilder createQueryBuilder();
}
