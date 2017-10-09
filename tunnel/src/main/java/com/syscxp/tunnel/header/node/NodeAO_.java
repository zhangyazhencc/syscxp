package com.syscxp.tunnel.header.node;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-08-22
 */
@StaticMetamodel(NodeAO.class)
public class NodeAO_ {
    public static volatile SingularAttribute<NodeAO, String> uuid;
    public static volatile SingularAttribute<NodeAO, String> name;
    public static volatile SingularAttribute<NodeAO, String> code;
    public static volatile SingularAttribute<NodeAO, String> extensionInfoUuid;
    public static volatile SingularAttribute<NodeAO, String> description;
    public static volatile SingularAttribute<NodeAO, String> contact;
    public static volatile SingularAttribute<NodeAO, String> telephone;
    public static volatile SingularAttribute<NodeAO, String> province;
    public static volatile SingularAttribute<NodeAO, String> city;
    public static volatile SingularAttribute<NodeAO, String> address;
    public static volatile SingularAttribute<NodeAO, Double> longtitude;
    public static volatile SingularAttribute<NodeAO, Double> latitude;
    public static volatile SingularAttribute<NodeAO, String> property;
    public static volatile SingularAttribute<NodeAO, NodeStatus> status;
    public static volatile SingularAttribute<NodeAO, Timestamp> createDate;
    public static volatile SingularAttribute<NodeAO, Timestamp> lastOpDate;
}
