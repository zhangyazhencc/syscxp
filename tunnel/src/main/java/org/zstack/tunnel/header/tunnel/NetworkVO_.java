package org.zstack.tunnel.header.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-05
 */
@StaticMetamodel(NetworkVO.class)
public class NetworkVO_ {
    public static volatile SingularAttribute<NetworkVO, String> uuid;
    public static volatile SingularAttribute<NetworkVO, String> accountUuid;
    public static volatile SingularAttribute<NetworkVO, String> name;
    public static volatile SingularAttribute<NetworkVO, Integer> vsi;
    public static volatile SingularAttribute<NetworkVO, String> monitorCidr;
    public static volatile SingularAttribute<NetworkVO, String> description;
    public static volatile SingularAttribute<NetworkVO, Timestamp> createDate;
    public static volatile SingularAttribute<NetworkVO, Timestamp> lastOpDate;
}
