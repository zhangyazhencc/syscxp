package com.syscxp.header.tunnel.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/25
 */
@StaticMetamodel(TaskResourceVO.class)
public class TaskResourceVO_ {
    public static volatile SingularAttribute<TaskResourceVO, String> uuid;
    public static volatile SingularAttribute<TaskResourceVO, String> resourceUuid;
    public static volatile SingularAttribute<TaskResourceVO, String> resourceType;
    public static volatile SingularAttribute<TaskResourceVO, TaskType> taskType;
    public static volatile SingularAttribute<TaskResourceVO, String> body;
    public static volatile SingularAttribute<TaskResourceVO, String> result;
    public static volatile SingularAttribute<TaskResourceVO, TaskStatus> status;
    public static volatile SingularAttribute<TaskResourceVO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<TaskResourceVO, Timestamp> createDate;
}
