package org.zstack.tunnel.header.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-08
 */
@StaticMetamodel(InterfaceAO.class)
public class InterfaceAO_ {
    public static volatile SingularAttribute<InterfaceAO, String> uuid;
    public static volatile SingularAttribute<InterfaceAO, String> accountUuid;
    public static volatile SingularAttribute<InterfaceAO, String> name;
    public static volatile SingularAttribute<InterfaceAO, String> switchPortUuid;
    public static volatile SingularAttribute<InterfaceAO, String> endpointUuid;
    public static volatile SingularAttribute<InterfaceAO, String> bandwidth;
    public static volatile SingularAttribute<InterfaceAO, String> isExclusive;
    public static volatile SingularAttribute<InterfaceAO, String> description;
    public static volatile SingularAttribute<InterfaceAO, Integer> months;
    public static volatile SingularAttribute<InterfaceAO, String> expiredDate;
    public static volatile SingularAttribute<InterfaceAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<InterfaceAO, Timestamp> createDate;
}
