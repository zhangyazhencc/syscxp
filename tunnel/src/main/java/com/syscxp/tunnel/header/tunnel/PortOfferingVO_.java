package com.syscxp.tunnel.header.tunnel;

import com.syscxp.tunnel.header.switchs.SwitchPortType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/10/30
 */
@StaticMetamodel(PortOfferingVO.class)
public class PortOfferingVO_ {

    public static volatile SingularAttribute<PortOfferingVO, String> uuid;
    public static volatile SingularAttribute<PortOfferingVO, String> name;
    public static volatile SingularAttribute<PortOfferingVO, SwitchPortType> type;
    public static volatile SingularAttribute<PortOfferingVO, String> description;
    public static volatile SingularAttribute<PortOfferingVO, Timestamp> createDate;
    public static volatile SingularAttribute<PortOfferingVO, Timestamp> lastOpDate;
}
