package com.syscxp.header.tunnel.sla;

import com.syscxp.header.tunnel.monitor.TunnelMonitorVO;
import org.apache.commons.net.ntp.TimeStamp;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-07.
 * @Description: .
 */
@StaticMetamodel(SlaAnalyzeVO.class)
public class SlaAnalyzeVO_ {
    public static volatile SingularAttribute<SlaAnalyzeVO,String> uuid;
    public static volatile SingularAttribute<SlaAnalyzeVO, Integer> batchNum;
    public static volatile SingularAttribute<SlaAnalyzeVO, String> summaryUuid;
    public static volatile SingularAttribute<SlaAnalyzeVO, String> endpoint;
    public static volatile SingularAttribute<SlaAnalyzeVO, String> level;
    public static volatile SingularAttribute<SlaAnalyzeVO, Long> start;
    public static volatile SingularAttribute<SlaAnalyzeVO, Long> end;
}
