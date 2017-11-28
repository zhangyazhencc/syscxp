package com.syscxp.header.host;

import com.syscxp.header.vo.ResourceVO_;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 */
@StaticMetamodel(HostAO.class)
public class HostAO_ {
    public static volatile SingularAttribute<HostAO, String> uuid;
    public static volatile SingularAttribute<HostAO, String> position;
    public static volatile SingularAttribute<HostAO, String> name;
    public static volatile SingularAttribute<HostAO, String> code;
    public static volatile SingularAttribute<HostAO, String> hostIp;
    public static volatile SingularAttribute<HostAO, HostState> state;
    public static volatile SingularAttribute<HostAO, HostStatus> status;
    public static volatile SingularAttribute<HostAO, String> hostType;
    public static volatile SingularAttribute<HostAO, Timestamp> createDate;
    public static volatile SingularAttribute<HostAO, Timestamp> lastOpDate;
}
