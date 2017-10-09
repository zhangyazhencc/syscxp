package com.syscxp.query;

import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.header.query.QueryBuilder;
import com.syscxp.header.query.QueryBuilderFactory;
import com.syscxp.header.query.QueryBuilderType;

public class MysqlQueryBuilderFactory implements QueryBuilderFactory {
    public static QueryBuilderType type = new QueryBuilderType("Mysql");
    
    @Autowired
    private MysqlQueryBuilderImpl3 builder;
    
    @Override
    public QueryBuilderType getQueryBuilderType() {
        return type;
    }

    @Override
    public QueryBuilder createQueryBuilder() {
        return builder;
    }

}
