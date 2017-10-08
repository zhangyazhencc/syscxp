package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.billing.ProductChargeModel;

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
    public static volatile SingularAttribute<InterfaceAO, Long> bandwidth;
    public static volatile SingularAttribute<InterfaceAO, String> description;
    public static volatile SingularAttribute<InterfaceAO, InterfaceState> state;
    public static volatile SingularAttribute<InterfaceAO, Integer> duration;
    public static volatile SingularAttribute<InterfaceAO, ProductChargeModel> productChargeModel;
    public static volatile SingularAttribute<InterfaceAO, Integer> maxModifies;
    public static volatile SingularAttribute<InterfaceAO, Timestamp> expiredDate;
    public static volatile SingularAttribute<InterfaceAO, Timestamp> lastOpDate;
    public static volatile SingularAttribute<InterfaceAO, Timestamp> createDate;
}
