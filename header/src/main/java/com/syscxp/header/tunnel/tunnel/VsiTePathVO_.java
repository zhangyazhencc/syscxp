package com.syscxp.header.tunnel.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/5/14
 */
@StaticMetamodel(VsiTePathVO.class)
public class VsiTePathVO_ {
    public static volatile SingularAttribute<VsiTePathVO, String> uuid;
    public static volatile SingularAttribute<VsiTePathVO, String> tunnelUuid;
    public static volatile SingularAttribute<VsiTePathVO, String> name;
    public static volatile SingularAttribute<VsiTePathVO, String> source;
    public static volatile SingularAttribute<VsiTePathVO, String> destination;
    public static volatile SingularAttribute<VsiTePathVO, String> direction;
    public static volatile SingularAttribute<VsiTePathVO, String> tnlPolicyName;
    public static volatile SingularAttribute<VsiTePathVO, String> tnlPolicydestination;
    public static volatile SingularAttribute<VsiTePathVO, Timestamp> createDate;
    public static volatile SingularAttribute<VsiTePathVO, Timestamp> lastOpDate;
}
