package org.zstack.tunnel.header.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-05
 */
@StaticMetamodel(NetWorkVO.class)
public class NetWorkVO_ {
    public static volatile SingularAttribute<NetWorkVO, String> uuid;
    public static volatile SingularAttribute<NetWorkVO, String> accountUuid;
    public static volatile SingularAttribute<NetWorkVO, String> name;
    public static volatile SingularAttribute<NetWorkVO, Integer> vsi;
    public static volatile SingularAttribute<NetWorkVO, String> monitorIp;
    public static volatile SingularAttribute<NetWorkVO, String> description;
    public static volatile SingularAttribute<NetWorkVO, Timestamp> createDate;
    public static volatile SingularAttribute<NetWorkVO, Timestamp> lastOpDate;
}
