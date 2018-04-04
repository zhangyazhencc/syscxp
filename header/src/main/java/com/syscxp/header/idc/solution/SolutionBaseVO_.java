package com.syscxp.header.idc.solution;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/11/20.
 */
@StaticMetamodel(SolutionBaseVO.class)
public class SolutionBaseVO_ {

    public static volatile SingularAttribute<SolutionBaseVO, String> uuid;
    public static volatile SingularAttribute<SolutionBaseVO, String> solutionUuid;
    public static volatile SingularAttribute<SolutionBaseVO, String> name;
    public static volatile SingularAttribute<SolutionBaseVO, BigDecimal> cost;
    public static volatile SingularAttribute<SolutionBaseVO, BigDecimal> discount;
    public static volatile SingularAttribute<SolutionBaseVO, BigDecimal> shareDiscount;
    public static volatile SingularAttribute<SolutionBaseVO, String> productChargeModel;
    public static volatile SingularAttribute<SolutionBaseVO, Integer> duration;
    public static volatile SingularAttribute<SolutionBaseVO, Timestamp> createDate;
    public static volatile SingularAttribute<SolutionBaseVO, Timestamp> lastOpDate;

}
