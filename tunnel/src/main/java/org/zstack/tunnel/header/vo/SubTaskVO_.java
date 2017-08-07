package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(SubTaskVO.class)
public class SubTaskVO_ {
    public static volatile SingularAttribute<SubTaskVO, String> uuid;
    public static volatile SingularAttribute<SubTaskVO, String> taskUuid;
    public static volatile SingularAttribute<SubTaskVO, String> agentUuid;
    public static volatile SingularAttribute<SubTaskVO, String> name;
    public static volatile SingularAttribute<SubTaskVO, String> seq;
    public static volatile SingularAttribute<SubTaskVO, String> body;
    public static volatile SingularAttribute<SubTaskVO, String> result;
    public static volatile SingularAttribute<SubTaskVO, String> status;
    public static volatile SingularAttribute<SubTaskVO, Timestamp> createDate;
    public static volatile SingularAttribute<SubTaskVO, Timestamp> lastOpDate;

}
