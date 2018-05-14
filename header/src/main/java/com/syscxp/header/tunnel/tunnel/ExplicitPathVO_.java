package com.syscxp.header.tunnel.tunnel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2018/5/14
 */
@StaticMetamodel(ExplicitPathVO.class)
public class ExplicitPathVO_ {
    public static volatile SingularAttribute<ExplicitPathVO, String> uuid;
    public static volatile SingularAttribute<ExplicitPathVO, String> vsiTePathUuid;
    public static volatile SingularAttribute<ExplicitPathVO, Integer> traceSort;
    public static volatile SingularAttribute<ExplicitPathVO, String> switchName;
    public static volatile SingularAttribute<ExplicitPathVO, String> switchIP;
    public static volatile SingularAttribute<ExplicitPathVO, String> tunnelsName;
    public static volatile SingularAttribute<ExplicitPathVO, String> explicitName;
    public static volatile SingularAttribute<ExplicitPathVO, Timestamp> createDate;
    public static volatile SingularAttribute<ExplicitPathVO, Timestamp> lastOpDate;
}
