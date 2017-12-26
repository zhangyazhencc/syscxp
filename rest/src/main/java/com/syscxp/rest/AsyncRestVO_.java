package com.syscxp.rest;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 15:07
 * Author: wj
 */
@StaticMetamodel(AsyncRestVO.class)
public class AsyncRestVO_ {
    public static volatile SingularAttribute<AsyncRestVO, String> uuid;
    public static volatile SingularAttribute<AsyncRestVO, String> requestData;
    public static volatile SingularAttribute<AsyncRestVO, AsyncRestState> state;
    public static volatile SingularAttribute<AsyncRestVO, String> result;
    public static volatile SingularAttribute<AsyncRestVO, Timestamp> createDate;
    public static volatile SingularAttribute<AsyncRestVO, Timestamp> lastOpDate;
}
