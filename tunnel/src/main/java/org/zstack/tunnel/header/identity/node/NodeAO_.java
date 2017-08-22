package org.zstack.tunnel.header.identity.node;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-22
 */
@StaticMetamodel(NodeAO.class)
public class NodeAO_ {
    public static volatile SingularAttribute<NodeVO, String> uuid;
    public static volatile SingularAttribute<NodeVO, String> name;
    public static volatile SingularAttribute<NodeVO, String> code;
    public static volatile SingularAttribute<NodeVO, String> extensionInfoUuid;
    public static volatile SingularAttribute<NodeVO, String> description;
    public static volatile SingularAttribute<NodeVO, String> contact;
    public static volatile SingularAttribute<NodeVO, String> telephone;
    public static volatile SingularAttribute<NodeVO, String> province;
    public static volatile SingularAttribute<NodeVO, String> city;
    public static volatile SingularAttribute<NodeVO, String> address;
    public static volatile SingularAttribute<NodeVO, Double> longtitude;
    public static volatile SingularAttribute<NodeVO, Double> latitude;
    public static volatile SingularAttribute<NodeVO, NodeProperty> property;
    public static volatile SingularAttribute<NodeVO, NodeStatus> status;
    public static volatile SingularAttribute<NodeVO, Timestamp> createDate;
    public static volatile SingularAttribute<NodeVO, Timestamp> lastOpDate;
}
