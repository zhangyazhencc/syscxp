package com.syscxp.header.tunnel.solution;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

@StaticMetamodel(ShareSolutionVO.class)
public class ShareSolutionVO_ {
    public static volatile SingularAttribute<ShareSolutionVO, String> uuid;
    public static volatile SingularAttribute<ShareSolutionVO, String> accountUuid;
    public static volatile SingularAttribute<ShareSolutionVO, String> ownerAccountUuid;
    public static volatile SingularAttribute<ShareSolutionVO, String> solutionUuid;
    public static volatile SingularAttribute<SolutionVO, Timestamp> createDate;
    public static volatile SingularAttribute<SolutionVO, Timestamp> lastOpDate;
}
