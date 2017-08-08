package org.zstack.tunnel.header.vo;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

@StaticMetamodel(HostVO.class)
public class HostVO_ {
    public static volatile SingularAttribute<HostVO, String> uuid;
    public static volatile SingularAttribute<HostVO, String> name;
    public static volatile SingularAttribute<HostVO, String> code;
    public static volatile SingularAttribute<HostVO, String> ip;
    public static volatile SingularAttribute<HostVO, String> username;
    public static volatile SingularAttribute<HostVO, String> password;
    public static volatile SingularAttribute<HostVO, String> monitorState;
    public static volatile SingularAttribute<HostVO, String> monitorStatus;
    public static volatile SingularAttribute<HostVO, Integer> deleted;
    public static volatile SingularAttribute<HostVO, Timestamp> createDate;
    public static volatile SingularAttribute<HostVO, Timestamp> lastOpDate;
}
