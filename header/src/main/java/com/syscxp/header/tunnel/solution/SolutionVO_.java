package com.syscxp.header.tunnel.solution;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/11/20.
 */

@StaticMetamodel(SolutionVO.class)
public class SolutionVO_ {
    public static volatile SingularAttribute<SolutionVO, String> uuid;
    public static volatile SingularAttribute<SolutionVO, String> accountUuid;
    public static volatile SingularAttribute<SolutionVO, String> name;
    public static volatile SingularAttribute<SolutionVO, String> description;
    public static volatile SingularAttribute<SolutionVO, String> totalCost;
    public static volatile SingularAttribute<SolutionVO, Timestamp> createDate;
    public static volatile SingularAttribute<SolutionVO, Timestamp> lastOpDate;
}
