package org.zstack.account.header.log;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(OperLogVO.class)
public class OperLogVO_ {
    public static volatile SingularAttribute<OperLogVO, String> uuid;
    public static volatile SingularAttribute<OperLogVO, String> accountUuid;
    public static volatile SingularAttribute<OperLogVO, String> userUuid;
    public static volatile SingularAttribute<OperLogVO, String> operator;
    public static volatile SingularAttribute<OperLogVO, String> category;
    public static volatile SingularAttribute<OperLogVO, String> action;
    public static volatile SingularAttribute<OperLogVO, String> resourceUuid;
    public static volatile SingularAttribute<OperLogVO, String> resourceType;
    public static volatile SingularAttribute<OperLogVO, OperStatus> status;
    public static volatile SingularAttribute<OperLogVO, String> description;
    public static volatile SingularAttribute<OperLogVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<OperLogVO, Timestamp> createDate;
}
