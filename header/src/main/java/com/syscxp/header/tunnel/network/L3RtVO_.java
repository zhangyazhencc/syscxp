package com.syscxp.header.tunnel.network;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(L3RtVO.class)
public class L3RtVO_ {
    public static volatile SingularAttribute<L3RtVO,String> uuid;
    public static volatile SingularAttribute<L3RtVO,String> l3EndpointUuid;
    public static volatile SingularAttribute<L3RtVO,String> impor;
    public static volatile SingularAttribute<L3RtVO,String> export;
    public static volatile SingularAttribute<L3RtVO,Timestamp> lastOpDate;
    public static volatile SingularAttribute<L3RtVO,Timestamp> createDate;
}
