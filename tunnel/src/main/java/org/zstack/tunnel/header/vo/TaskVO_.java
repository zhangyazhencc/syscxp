package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(TaskVO.class)
public class TaskVO_ {
    public static volatile SingularAttribute<TaskVO, String> uuid;
    public static volatile SingularAttribute<TaskVO, String> name;
    public static volatile SingularAttribute<TaskVO, String> status;
    public static volatile SingularAttribute<TaskVO, String> objectUuid;
    public static volatile SingularAttribute<TaskVO, String> objectType;
    public static volatile SingularAttribute<TaskVO, String> body;
    public static volatile SingularAttribute<TaskVO, Timestamp> createDate;
    public static volatile SingularAttribute<TaskVO, Timestamp> lastOpDate;
}
