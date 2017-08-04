package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(DeployTaskVO.class)
public class DeployTaskVO_ {
    public static volatile SingularAttribute<DeployTaskVO, String> uuid;
    public static volatile SingularAttribute<DeployTaskVO, String> tunnelPointUuid;
    public static volatile SingularAttribute<DeployTaskVO, String> state;
    public static volatile SingularAttribute<DeployTaskVO, String> type;
    public static volatile SingularAttribute<DeployTaskVO, String> description;
    public static volatile SingularAttribute<DeployTaskVO, String> finshBy;
    public static volatile SingularAttribute<DeployTaskVO, Timestamp> createDate;
    public static volatile SingularAttribute<DeployTaskVO, Timestamp> lastOpDate;
}
