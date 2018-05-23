package com.syscxp.header.tunnel.sla;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-07.
 * @Description: .
 */
@StaticMetamodel(SlaAnalyzeSummaryVO.class)
public class SlaAnalyzeSummaryVO_ {
    public static volatile SingularAttribute<SlaAnalyzeSummaryVO,String> uuid;
    public static volatile SingularAttribute<SlaAnalyzeSummaryVO, Integer> batchNum;
    public static volatile SingularAttribute<SlaAnalyzeSummaryVO, String> productUuid;
    public static volatile SingularAttribute<SlaAnalyzeSummaryVO, String> productType;
    public static volatile SingularAttribute<SlaAnalyzeSummaryVO, Double> availableRate;
    public static volatile SingularAttribute<SlaAnalyzeSummaryVO, Double> unnormalRate;
    public static volatile SingularAttribute<SlaAnalyzeSummaryVO, Boolean> isSuccess;
}
