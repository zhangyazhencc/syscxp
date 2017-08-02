package org.zstack.billing.header.identity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.zstack.header.search.Inventory;

@Inventory(mappingVOClass=AccountBalanceVO.class)
public class AccountBalanceInventory {

	  private String uuid;
	  private BigDecimal presentBalance;
	  private BigDecimal creditPoint;
	  private BigDecimal cashBalance;
	  private Timestamp createDate;
     private Timestamp lastOpDate;
     
     
     public static AccountBalanceInventory valueOf(AccountBalanceVO vo) {
         AccountBalanceInventory inv = new AccountBalanceInventory();
         inv.setUuid(vo.getUuid());
         inv.setCashBalance(vo.getCashBalance());
         inv.setPresentBalance(vo.getPresentBalance());
         inv.setCreditPoint(vo.getCreditPoint());
         inv.setCreateDate(vo.getCreateDate());
         inv.setLastOpDate(vo.getLastOpDate());
         return inv;
     }
     
     public static List<AccountBalanceInventory> valueOf(Collection<AccountBalanceVO> vos) {
         List<AccountBalanceInventory> lst = new ArrayList<AccountBalanceInventory>(vos.size());
         for (AccountBalanceVO vo : vos) {
             lst.add(AccountBalanceInventory.valueOf(vo));
         }
         return lst;
     }
     
     
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public BigDecimal getPresentBalance() {
		return presentBalance;
	}
	public void setPresentBalance(BigDecimal presentBalance) {
		this.presentBalance = presentBalance;
	}
	public BigDecimal getCreditPoint() {
		return creditPoint;
	}
	public void setCreditPoint(BigDecimal creditPoint) {
		this.creditPoint = creditPoint;
	}
	public BigDecimal getCashBalance() {
		return cashBalance;
	}
	public void setCashBalance(BigDecimal cashBalance) {
		this.cashBalance = cashBalance;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public Timestamp getLastOpDate() {
		return lastOpDate;
	}
	public void setLastOpDate(Timestamp lastOpDate) {
		this.lastOpDate = lastOpDate;
	}
     
     
}
