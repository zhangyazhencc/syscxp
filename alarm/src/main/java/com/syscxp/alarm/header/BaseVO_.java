package com.syscxp.alarm.header;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(BaseVO.class)
public class BaseVO_ {

    public static volatile SingularAttribute<BaseVO, String> uuid;
    public static volatile SingularAttribute<BaseVO, Timestamp> createDate;
    public static volatile SingularAttribute<BaseVO, Timestamp> lastOpDate;

}
