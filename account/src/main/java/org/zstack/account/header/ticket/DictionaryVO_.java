package org.zstack.account.header.ticket;


import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/9/26.
 */

@StaticMetamodel(DictionaryVO.class)
public class DictionaryVO_ {
    public static volatile SingularAttribute<DictionaryVO, Long> id;
    public static volatile SingularAttribute<DictionaryVO, String> dictName;
    public static volatile SingularAttribute<DictionaryVO, String> dictKey;
    public static volatile SingularAttribute<DictionaryVO, String> dictValue;
    public static volatile SingularAttribute<DictionaryVO, String> valueName;
    public static volatile SingularAttribute<DictionaryVO, Timestamp> createDate;
    public static volatile SingularAttribute<DictionaryVO, Timestamp> lastOpDate;
}
