package com.syscxp.header.configuration;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/30
 */
@StaticMetamodel(BandwidthOfferingVO.class)
public class BandwidthOfferingVO_ {

    public static volatile SingularAttribute<BandwidthOfferingVO, String> uuid;
    public static volatile SingularAttribute<BandwidthOfferingVO, String> name;
    public static volatile SingularAttribute<BandwidthOfferingVO, String> description;
    public static volatile SingularAttribute<BandwidthOfferingVO, Long> bandwidth;
    public static volatile SingularAttribute<BandwidthOfferingVO, Timestamp> createDate;
    public static volatile SingularAttribute<BandwidthOfferingVO, Timestamp> lastOpDate;
}
