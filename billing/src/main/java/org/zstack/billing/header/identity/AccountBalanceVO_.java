package org.zstack.billing.header.identity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;


@StaticMetamodel(AccountBalanceVO.class)
public class AccountBalanceVO_ {
	
	public static volatile SingularAttribute<AccountBalanceVO, String> uuid;
	public static volatile SingularAttribute<AccountBalanceVO, BigDecimal> presentBalance;
	public static volatile SingularAttribute<AccountBalanceVO, BigDecimal> creditPoint;
	public static volatile SingularAttribute<AccountBalanceVO, BigDecimal> cashBalance;
	public static volatile SingularAttribute<AccountBalanceVO, Timestamp> createDate;
	public static volatile SingularAttribute<AccountBalanceVO, Timestamp> lastOpDate;
	
}
