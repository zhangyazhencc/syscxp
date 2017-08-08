package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(NetworkTypeVO.class)
public class NetworkTypeVO_ {
    public static volatile SingularAttribute<NetworkTypeVO, String> uuid;
    public static volatile SingularAttribute<NetworkTypeVO, String> name;
    public static volatile SingularAttribute<NetworkTypeVO, String> code;
    public static volatile SingularAttribute<NetworkTypeVO, Timestamp> createDate;
    public static volatile SingularAttribute<NetworkTypeVO, Timestamp> lastOpDate;
}
