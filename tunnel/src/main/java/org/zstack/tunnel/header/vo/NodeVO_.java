package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(NodeVO.class)
public class NodeVO_ {
    public static volatile SingularAttribute<NodeVO, String> uuid;
    public static volatile SingularAttribute<NodeVO, String> name;
    public static volatile SingularAttribute<NodeVO, String> code;
    public static volatile SingularAttribute<NodeVO, String> extensionInfoUuid;
    public static volatile SingularAttribute<NodeVO, String> description;
    public static volatile SingularAttribute<NodeVO, String> contact;
    public static volatile SingularAttribute<NodeVO, String> telephone;
    public static volatile SingularAttribute<NodeVO, String> province;
    public static volatile SingularAttribute<NodeVO, String> city;
    public static volatile SingularAttribute<NodeVO, Double> longtitude;
    public static volatile SingularAttribute<NodeVO, Double> latitude;
    public static volatile SingularAttribute<NodeVO, String> property;
    public static volatile SingularAttribute<NodeVO, String> status;
    public static volatile SingularAttribute<NodeVO, Integer> deleted;
    public static volatile SingularAttribute<NodeVO, Timestamp> createDate;
    public static volatile SingularAttribute<NodeVO, Timestamp> lastOpDate;
}
